package com.group02.mobile.viewmodel

import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.group02.mobile.data.remote.JishoService
import com.group02.mobile.utils.RomajiUtils
import com.group02.mobile.utils.TranslationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

enum class FilterType {
    ALL, LEARNED, NOT_LEARNED
}

data class DictionaryWord(
    val word: String = "",
    val hiragana: String = "",
    val romaji: String = "",
    val meanings: String = "",
    val isLearned: Boolean = false
)

class DictionaryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val jishoService = JishoService()

    val levels = listOf("N5", "N4", "N3", "N2", "N1")

    private val _currentLevel = MutableStateFlow(levels[0])
    val currentLevel: StateFlow<String> = _currentLevel

    private val _words = MutableStateFlow<List<DictionaryWord>>(emptyList())
    
    // Real-time learned word IDs
    private val _learnedWordIds = MutableStateFlow<Set<String>>(emptySet())

    private val _filterType = MutableStateFlow(FilterType.ALL)
    val filterType: StateFlow<FilterType> = _filterType

    val words: StateFlow<List<DictionaryWord>> = combine(_words, _learnedWordIds, _filterType) { words, learnedIds, filter ->
        val updatedWords = words.map { 
            val id = it.word.ifEmpty { it.hiragana }
            it.copy(isLearned = id in learnedIds)
        }
        when (filter) {
            FilterType.ALL -> updatedWords
            FilterType.LEARNED -> updatedWords.filter { it.isLearned }
            FilterType.NOT_LEARNED -> updatedWords.filter { !it.isLearned }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(value = false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isEndReached = MutableStateFlow(false)
    val isEndReached: StateFlow<Boolean> = _isEndReached

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private var lastVisibleDocument: DocumentSnapshot? = null
    private val pageSize = 10L

    init {
        observeLearnedWords()
        loadWords(isFirstPage = true)
    }

    private fun observeLearnedWords() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .collection("learned_words")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val ids = snapshot?.documents?.map { it.id }?.toSet() ?: emptySet()
                _learnedWordIds.value = ids
            }
    }

    fun setFilterType(filterType: FilterType) {
        _filterType.value = filterType
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            loadWords(isFirstPage = true)
        }
    }

    fun performSearch() {
        val query = _searchQuery.value
        if (query.isNotEmpty()) {
            searchWords(query)
        }
    }

    fun toggleLearned(dictionaryWord: DictionaryWord) {
        val userId = auth.currentUser?.uid ?: return
        val wordId = dictionaryWord.word.ifEmpty { dictionaryWord.hiragana }
        if (wordId.isEmpty()) return

        viewModelScope.launch {
            try {
                val learnedRef = db.collection("users").document(userId)
                    .collection("learned_words").document(wordId)

                if (dictionaryWord.isLearned) {
                    learnedRef.delete().await()
                } else {
                    val data = mapOf(
                        "wordId" to wordId,
                        "word" to dictionaryWord.word,
                        "hiragana" to dictionaryWord.hiragana,
                        "romaji" to dictionaryWord.romaji,
                        "meanings" to dictionaryWord.meanings,
                        "learned" to true, 
                        "timestamp" to System.currentTimeMillis(),
                        "repetition" to 0,
                        "interval" to 0,
                        "easeFactor" to 2.5f,
                        "nextReviewTime" to System.currentTimeMillis() // Ready to review immediately
                    )
                    learnedRef.set(data).await()
                }
            } catch (e: Exception) {
                _error.value = "Không thể cập nhật trạng thái: ${e.message}"
            }
        }
    }

    private fun searchWords(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            _isLoading.value = true
            _words.value = emptyList()
            _isEndReached.value = true // Disable pagination during search

            try {
                // Determine if query is Vietnamese and translate it to English for Jisho
                val searchTerms = TranslationUtils.translateToEnglish(query).lowercase().trim()
                
                // Call Jisho API
                val jishoResults = jishoService.searchWords(searchTerms)
                
                if (jishoResults.isEmpty()) {
                    _words.value = emptyList()
                    return@launch
                }

                // Translate results back to Vietnamese
                val translatedResults = coroutineScope {
                    jishoResults.take(15).map { word ->
                        async {
                            word.copy(
                                meanings = TranslationUtils.translateMeaningsString(word.meanings)
                            )
                        }
                    }.awaitAll()
                }

                _words.value = translatedResults
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred during search"
            } finally {
                _isLoading.value = false
                _isSearching.value = false
            }
        }
    }

    fun setLevel(level: String) {
        if (_currentLevel.value != level) {
            _currentLevel.value = level
            loadWords(isFirstPage = true)
        }
    }

    fun loadMoreWords() {
        if (!_isLoading.value && !_isEndReached.value) {
            loadWords(isFirstPage = false)
        }
    }

    private fun loadWords(isFirstPage: Boolean) {
        viewModelScope.launch {
            if (isFirstPage) {
                _words.value = emptyList()
                lastVisibleDocument = null
                _isEndReached.value = false
                // Small delay to ensure network/environment stability on app startup
                delay(300)
            }

            _isLoading.value = true
            _error.value = null

            try {
                val collectionName = "TuVung_${_currentLevel.value}"
                var query: Query = db.collection(collectionName)
                    .orderBy(com.google.firebase.firestore.FieldPath.documentId())
                    .limit(pageSize)

                if (!isFirstPage && (lastVisibleDocument != null)) {
                    query = query.startAfter(lastVisibleDocument!!)
                }

                val snapshot = query.get().await()

                if (!snapshot.isEmpty) {
                    lastVisibleDocument = snapshot.documents[snapshot.size() - 1]
                    val rawWords = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(DictionaryWord::class.java)
                    }
                    
                    val newWords = coroutineScope {
                        rawWords.map { word ->
                            async {
                                word.copy(
                                    meanings = TranslationUtils.translateMeaningsString(word.meanings)
                                )
                            }
                        }.awaitAll()
                    }

                    val currentList = _words.value.toMutableList()
                    currentList.addAll(newWords)
                    _words.value = currentList
                    
                    if (snapshot.size() < pageSize) {
                        _isEndReached.value = true
                    }
                } else {
                    _isEndReached.value = true
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred while loading words"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
