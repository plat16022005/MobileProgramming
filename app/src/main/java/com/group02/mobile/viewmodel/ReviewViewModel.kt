package com.group02.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.group02.mobile.data.model.srs.SrsData
import com.group02.mobile.utils.TranslationUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReviewViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ── Review queue ────────────────────────────────────────────────────────────
    private val _wordsToReview = MutableStateFlow<List<SrsData>>(emptyList())
    val wordsToReview: StateFlow<List<SrsData>> = _wordsToReview

    // ── Daily new-word lesson ────────────────────────────────────────────────────
    /** 20 words selected for today's new lesson (persistent per day) */
    private val _dailyNewWords = MutableStateFlow<List<SrsData>>(emptyList())
    val dailyNewWords: StateFlow<List<SrsData>> = _dailyNewWords

    /** IDs of today's daily words that the user has already marked as learned */
    private val _learnedDailyWordIds = MutableStateFlow<Set<String>>(emptySet())
    val learnedDailyWordIds: StateFlow<Set<String>> = _learnedDailyWordIds

    // ── Counters ────────────────────────────────────────────────────────────────
    private val _newWordsToday = MutableStateFlow(0)
    val newWordsToday: StateFlow<Int> = _newWordsToday

    private val _masteredCount = MutableStateFlow(0)
    val masteredCount: StateFlow<Int> = _masteredCount

    // ── Status ──────────────────────────────────────────────────────────────────
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ────────────────────────────────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────────────────────────────────

    private fun todayKey(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun startOfDayMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /** Calculates the 6:00 AM timestamp N days from now */
    private fun nextReviewTimeAt6AM(days: Int): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        cal.set(Calendar.HOUR_OF_DAY, 6)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    // ────────────────────────────────────────────────────────────────────────────
    // fetchDailyPlan: loads review queue + new-word counters
    // ────────────────────────────────────────────────────────────────────────────
    fun fetchDailyPlan() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val snapshot = db.collection("users").document(userId)
                    .collection("learned_words")
                    .get()
                    .await()

                val allWords = snapshot.documents.mapNotNull { it.toObject(SrsData::class.java) }
                val currentTime = System.currentTimeMillis()

                // Words overdue for review (not mastered, nextReviewTime passed)
                val rawToReview = allWords
                    .filter { !it.isMastered && it.nextReviewTime <= currentTime }
                    .shuffled()

                // Translate meanings for review words in parallel
                val toReview = coroutineScope {
                    rawToReview.map { word ->
                        async {
                            word.copy(meanings = TranslationUtils.translateMeaningsString(word.meanings))
                        }
                    }.awaitAll()
                }

                _masteredCount.value = allWords.count { it.isMastered }

                // New words learned today (repetition == 0, added today)
                val todayStart = startOfDayMillis()
                val newWordsCount = allWords.count { it.repetition == 0 && it.timestamp >= todayStart }

                _wordsToReview.value = toReview
                _newWordsToday.value = newWordsCount
            } catch (e: Exception) {
                _error.value = "Không thể tải kế hoạch học tập: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // fetchDailyNewWords — carry-over logic:
    //   today's plan = unfinished words from last plan
    //                + new random words to fill up to 20
    //   If previous plan had 20 words and 0 were learned → same 20 words, no new.
    //   If previous plan had 20 words and 10 learned → 10 carry + 10 new = 20.
    // ────────────────────────────────────────────────────────────────────────────
    fun fetchDailyNewWords() {
        val userId = auth.currentUser?.uid ?: return
        val today = todayKey()

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // 1. Today's plan already exists → just load it
                val planRef = db.collection("users").document(userId)
                    .collection("daily_plans").document(today)
                val planDoc = planRef.get().await()

                if (planDoc.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    val savedIds = planDoc.get("wordIds") as? List<String> ?: emptyList()
                    val level = planDoc.getString("level") ?: "N5"
                    // Also figure out which of these are already learned
                    val learnedSnap = db.collection("users").document(userId)
                        .collection("learned_words").get().await()
                    val learnedIds = learnedSnap.documents.map { it.id }.toSet()
                    _learnedDailyWordIds.value = savedIds.filter { it in learnedIds }.toSet()
                    _dailyNewWords.value = fetchWordsByIds(level, savedIds)
                    return@launch
                }

                // 2. Get user's study level
                val profileDoc = db.collection("profiles").document(userId).get().await()
                val studyLevel = profileDoc.getString("studyLevel") ?: "N5"
                val normalizedLevel = normalizeLevel(studyLevel)

                // 3. Get already-learned word IDs
                val learnedSnapshot = db.collection("users").document(userId)
                    .collection("learned_words").get().await()
                val learnedIds = learnedSnapshot.documents.map { it.id }.toSet()

                // 4. Find the most recent previous daily plan (for carry-over)
                val previousPlansSnapshot = db.collection("users").document(userId)
                    .collection("daily_plans")
                    .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(1)
                    .get().await()

                val unfinishedIds = mutableListOf<String>()
                var previousLevel = normalizedLevel

                if (!previousPlansSnapshot.isEmpty) {
                    val prevPlan = previousPlansSnapshot.documents.first()
                    @Suppress("UNCHECKED_CAST")
                    val prevWordIds = prevPlan.get("wordIds") as? List<String> ?: emptyList()
                    previousLevel = prevPlan.getString("level") ?: normalizedLevel
                    // Unfinished = in previous plan but NOT yet in learned_words
                    unfinishedIds.addAll(prevWordIds.filter { it !in learnedIds })
                }

                // 5. How many slots are left for new words?
                val newSlotsNeeded = (20 - unfinishedIds.size).coerceAtLeast(0)

                // 6. Fetch unfinished word details
                val unfinishedWords = if (unfinishedIds.isNotEmpty())
                    fetchWordsByIds(previousLevel, unfinishedIds)
                else emptyList()

                // 7. Pick fresh random words to fill remaining slots
                val newWords = if (newSlotsNeeded > 0) {
                    val excludeIds = learnedIds + unfinishedIds.toSet()
                    val vocabSnapshot = db.collection("TuVung_$normalizedLevel").get().await()
                    val rawNewWords = vocabSnapshot.documents
                        .filter { it.id !in excludeIds }
                        .map { doc ->
                            SrsData(
                                wordId = doc.id,
                                word = doc.getString("word") ?: "",
                                hiragana = doc.getString("hiragana") ?: "",
                                romaji = doc.getString("romaji") ?: "",
                                meanings = doc.getString("meanings") ?: ""
                            )
                        }
                        .shuffled()
                        .take(newSlotsNeeded)

                    // Translate meanings in parallel
                    coroutineScope {
                        rawNewWords.map { word ->
                            async {
                                word.copy(meanings = TranslationUtils.translateMeaningsString(word.meanings))
                            }
                        }.awaitAll()
                    }
                } else emptyList()

                // 8. Today = unfinished first, then new words (max 20 total)
                val todayWords = (unfinishedWords + newWords).take(20)

                // 9. Save today's plan
                planRef.set(
                    mapOf(
                        "date"    to today,
                        "level"   to normalizedLevel,
                        "wordIds" to todayWords.map { it.wordId }
                    )
                ).await()

                _dailyNewWords.value = todayWords
                _learnedDailyWordIds.value = emptySet() // fresh plan, nothing learned yet
            } catch (e: Exception) {
                _error.value = "Không thể tải từ mới hôm nay: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchWordsByIds(level: String, ids: List<String>): List<SrsData> = coroutineScope {
        if (ids.isEmpty()) return@coroutineScope emptyList()
        try {
            val snapshot = db.collection("TuVung_$level").get().await()
            val baseWords = snapshot.documents
                .filter { it.id in ids }
                .map { doc ->
                    SrsData(
                        wordId = doc.id,
                        word = doc.getString("word") ?: "",
                        hiragana = doc.getString("hiragana") ?: "",
                        romaji = doc.getString("romaji") ?: "",
                        meanings = doc.getString("meanings") ?: ""
                    )
                }

            // Translate meanings in parallel
            baseWords.map { word ->
                async {
                    word.copy(meanings = TranslationUtils.translateMeaningsString(word.meanings))
                }
            }.awaitAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** Map display level names like "N5 (Sơ cấp)" → "N5" */
    private fun normalizeLevel(raw: String): String {
        return when {
            raw.startsWith("N1") -> "N1"
            raw.startsWith("N2") -> "N2"
            raw.startsWith("N3") -> "N3"
            raw.startsWith("N4") -> "N4"
            else -> "N5"
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // SM-2 Algorithm (production — intervals in DAYS)
    //
    // Quality mapping:
    //   0 = Quên (Again) → reset, review tomorrow
    //   3 = Khó  (Hard)  → review in 1 day, ease factor drops
    //   4 = Tốt  (Good)  → standard SM-2
    //   5 = Dễ   (Easy)  → accelerated, ease factor increases
    // ────────────────────────────────────────────────────────────────────────────
    fun processReview(word: SrsData, quality: Int) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                var newRepetition = word.repetition
                var newInterval   = word.interval
                var newEaseFactor = word.easeFactor

                when {
                    quality == 0 -> {
                        // Forgot — reset completely, review in 1 day
                        newRepetition = 0
                        newInterval   = 1
                        newEaseFactor = (newEaseFactor - 0.20f).coerceAtLeast(1.3f)
                    }
                    quality == 3 -> {
                        // Hard — always 1 day, ease drops slightly
                        newInterval   = 1
                        newEaseFactor = (newEaseFactor - 0.15f).coerceAtLeast(1.3f)
                        newRepetition++
                    }
                    quality == 4 -> {
                        // Good — standard SM-2 progression
                        newInterval = when (newRepetition) {
                            0 -> 1   // first time: 1 day (tomorrow)
                            1 -> 3   // second time: 3 days
                            else -> Math.round(newInterval * newEaseFactor)
                        }
                        // EF unchanged for "good"
                        newRepetition++
                    }
                    quality == 5 -> {
                        // Easy — accelerated
                        newInterval = when (newRepetition) {
                            0 -> 2    // first time: 2 days
                            1 -> 6    // second time: 6 days
                            else -> Math.round(newInterval * newEaseFactor * 1.3f)
                        }
                        newEaseFactor = (newEaseFactor + 0.15f).coerceAtMost(3.0f)
                        newRepetition++
                    }
                }

                // Convert interval (days) to 6:00 AM of that day
                val nextReviewTime = nextReviewTimeAt6AM(newInterval)

                db.collection("users").document(userId)
                    .collection("learned_words")
                    .document(word.wordId)
                    .update(
                        mapOf(
                            "repetition"     to newRepetition,
                            "interval"       to newInterval,
                            "easeFactor"     to newEaseFactor,
                            "nextReviewTime" to nextReviewTime
                        )
                    )
                    .await()

                // Remove from current session queue
                _wordsToReview.value = _wordsToReview.value.filter { it.wordId != word.wordId }

            } catch (e: Exception) {
                _error.value = "Có lỗi xảy ra khi lưu kết quả ôn tập."
            }
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Mark a word as permanently mastered
    // ────────────────────────────────────────────────────────────────────────────
    fun markAsMastered(word: SrsData) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                // Double-safety: isMastered flag + push nextReviewTime to far future
                db.collection("users").document(userId)
                    .collection("learned_words")
                    .document(word.wordId)
                    .update(
                        mapOf(
                            "isMastered"     to true,
                            "nextReviewTime" to Long.MAX_VALUE
                        )
                    )
                    .await()

                _wordsToReview.value = _wordsToReview.value.filter { it.wordId != word.wordId }
                _masteredCount.value = _masteredCount.value + 1
            } catch (e: Exception) {
                _error.value = "Không thể đánh dấu từ này."
            }
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Mark a daily-lesson word as learned → adds it to the SRS review queue.
    // nextReviewTime = tomorrow so SM-2 starts the next day.
    // ────────────────────────────────────────────────────────────────────────────
    fun markWordAsLearned(word: SrsData) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val nextReview = nextReviewTimeAt6AM(1)
                val data = mapOf(
                    "wordId"         to word.wordId,
                    "word"           to word.word,
                    "hiragana"       to word.hiragana,
                    "romaji"         to word.romaji,
                    "meanings"       to word.meanings,
                    "learned"        to true,
                    "timestamp"      to System.currentTimeMillis(),
                    "repetition"     to 0,
                    "interval"       to 1,
                    "easeFactor"     to 2.5f,
                    "nextReviewTime" to nextReview,
                    "isMastered"     to false
                )
                db.collection("users").document(userId)
                    .collection("learned_words")
                    .document(word.wordId)
                    .set(data)
                    .await()

                // Update in-memory learned set and counter
                _learnedDailyWordIds.value = _learnedDailyWordIds.value + word.wordId
                _newWordsToday.value = _newWordsToday.value + 1
            } catch (e: Exception) {
                _error.value = "Không thể lưu từ đã học: ${e.message}"
            }
        }
    }
}
