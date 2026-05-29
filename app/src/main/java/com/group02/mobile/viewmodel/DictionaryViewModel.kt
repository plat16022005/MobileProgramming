package com.group02.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

    private var lastVisibleDocument: DocumentSnapshot? = null
    private val pageSize = 10L

    init {
        loadWords(isFirstPage = true)
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
                    val newWords = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(DictionaryWord::class.java)
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
