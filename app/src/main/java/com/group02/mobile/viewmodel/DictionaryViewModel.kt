package com.group02.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

data class DictionaryWord(
    val word: String = "",
    val hiragana: String = "",
    val romaji: String = "",
    val meanings: String = ""
)

class DictionaryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    val levels = listOf("N5", "N4", "N3", "N2", "N1")

    private val _currentLevel = MutableStateFlow(levels[0])
    val currentLevel: StateFlow<String> = _currentLevel

    private val _words = MutableStateFlow<List<DictionaryWord>>(emptyList())
    val words: StateFlow<List<DictionaryWord>> = _words

    private val _isLoading = MutableStateFlow(false)
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
        loadWords(isFirstPage = true)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            loadWords(isFirstPage = true)
        }
    }

    fun performSearch() {
        val query = _searchQuery.value
        if (query.length >= 2) {
            searchWords(query)
        }
    }

    private fun searchWords(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            _isLoading.value = true
            _words.value = emptyList()
            _isEndReached.value = true // Disable pagination during search for simplicity

            try {
                // Determine if query is Vietnamese and translate it
                val translatedQuery = TranslationUtils.translateToEnglish(query).lowercase().trim()
                val queryLower = query.lowercase().trim()
                
                // Convert Romaji to Hiragana for better matching
                val queryAsHiragana = RomajiUtils.toHiragana(queryLower)
                
                // Use a list of terms to search for in meanings
                val meaningSearchTerms = mutableListOf(translatedQuery)
                if (translatedQuery != queryLower) {
                    meaningSearchTerms.addAll(translatedQuery.split(" ").filter { it.length > 2 })
                }

                val allResults = mutableListOf<DictionaryWord>()
                
                // Search in all levels
                coroutineScope {
                    levels.map { level ->
                        async {
                            val collectionName = "TuVung_$level"
                            val snapshot = db.collection(collectionName).get().await()
                            
                            snapshot.documents.mapNotNull { doc ->
                                val word = doc.toObject(DictionaryWord::class.java) ?: return@mapNotNull null
                                val wordRomaji = word.romaji.lowercase().trim()
                                val wordHiragana = word.hiragana.trim()
                                val wordMeaningsLower = word.meanings.lowercase()
                                
                                // Scoring system for relevance
                                var score = 0
                                
                                // 1. Exact matches in Japanese fields (Highest priority)
                                if (word.word == query || wordHiragana == query || wordHiragana == queryAsHiragana || wordRomaji == queryLower) {
                                    score += 200
                                }
                                
                                // 2. Exact match in translated meanings
                                val meaningsList = wordMeaningsLower.split(",").map { it.trim() }
                                if (meaningsList.any { it == translatedQuery }) {
                                    score += 150
                                }
                                
                                // 3. Meaning starts with translated query
                                if (meaningsList.any { it.startsWith(translatedQuery) }) {
                                    score += 80
                                }

                                // 4. Starts with in Japanese fields
                                if (word.word.startsWith(query) || wordHiragana.startsWith(query) || wordHiragana.startsWith(queryAsHiragana) || wordRomaji.startsWith(queryLower)) {
                                    score += 50
                                }

                                // 5. Meaning contains search terms
                                for (term in meaningSearchTerms) {
                                    if (wordMeaningsLower.contains(term)) {
                                        score += if (term == translatedQuery) 30 else 10
                                    }
                                }

                                // 6. Japanese fields contain query (minimum 2 chars)
                                if (query.length >= 2) {
                                    if (word.word.contains(query) || wordHiragana.contains(query) || wordHiragana.contains(queryAsHiragana) || wordRomaji.contains(queryLower)) {
                                        score += 20
                                    }
                                }
                                
                                if (score > 0) word to score else null
                            }
                        }
                    }.awaitAll().flatten().let { resultsWithScore ->
                        // Sort by score descending, remove duplicates, and take top 15
                        allResults.addAll(
                            resultsWithScore
                                .sortedByDescending { it.second }
                                .map { it.first }
                                .distinctBy { it.word + it.hiragana }
                                .take(15)
                        )
                    }
                }

                // Translate results
                val translatedResults = coroutineScope {
                    allResults.distinctBy { it.word + it.hiragana }.map { word ->
                        async {
                            word.copy(meanings = TranslationUtils.translateMeaningsString(word.meanings))
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

                if (!isFirstPage && lastVisibleDocument != null) {
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
                                word.copy(meanings = TranslationUtils.translateMeaningsString(word.meanings))
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
