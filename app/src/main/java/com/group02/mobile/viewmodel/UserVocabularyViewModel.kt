package com.group02.mobile.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group02.mobile.data.local.AppDatabase
import com.group02.mobile.data.model.vocabulary.UserVocabulary
import com.group02.mobile.data.repository.UserVocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    object Success : UiState()
    data class Error(val message: String) : UiState()
}

class UserVocabularyViewModel(
    application: Application,
    private val repository: UserVocabularyRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val vocabularyList: StateFlow<List<UserVocabulary>> = repository.getAllVocabularies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredList: StateFlow<List<UserVocabulary>> = combine(
        vocabularyList,
        _searchQuery
    ) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            val lowercaseQuery = query.lowercase()
            list.filter {
                it.kanji.lowercase().contains(lowercaseQuery) ||
                it.hiragana.lowercase().contains(lowercaseQuery) ||
                it.romaji.lowercase().contains(lowercaseQuery) ||
                it.meaning.lowercase().contains(lowercaseQuery)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Optional: Sync from Firestore on startup
        viewModelScope.launch {
            repository.syncFromFirestore()
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun addVocabulary(kanji: String, hiragana: String, romaji: String, meaning: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val item = UserVocabulary(
                kanji = kanji,
                hiragana = hiragana,
                romaji = romaji,
                meaning = meaning
            )
            val result = repository.addVocabulary(item)
            if (result.isSuccess) {
                _uiState.value = UiState.Success
            } else {
                _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun updateVocabulary(item: UserVocabulary) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.updateVocabulary(item)
            if (result.isSuccess) {
                _uiState.value = UiState.Success
            } else {
                _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun deleteVocabulary(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.deleteVocabulary(id)
            if (result.isSuccess) {
                _uiState.value = UiState.Success
            } else {
                _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun importFromCsv(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val context = getApplication<Application>().applicationContext
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val reader = InputStreamReader(inputStream)
                    val content = reader.readText()
                    reader.close()
                    inputStream.close()
                    
                    val result = repository.importFromCsv(content)
                    if (result.isSuccess) {
                        _uiState.value = UiState.Success
                    } else {
                        _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Error importing CSV")
                    }
                } else {
                    _uiState.value = UiState.Error("Could not open file")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error importing CSV")
            }
        }
    }
    
    fun exportToCsv(context: Context) {
        viewModelScope.launch {
            try {
                val list = vocabularyList.value
                val csvContent = buildString {
                    append("Kanji,Hiragana,Romaji,Meaning\n")
                    list.forEach { vocab ->
                        val kanji = vocab.kanji.replace(",", " ")
                        val hiragana = vocab.hiragana.replace(",", " ")
                        val romaji = vocab.romaji.replace(",", " ")
                        val meaning = vocab.meaning.replace(",", " ")
                        append("$kanji,$hiragana,$romaji,$meaning\n")
                    }
                }

                // Save to cache dir
                val file = File(context.cacheDir, "my_vocabulary.csv")
                val writer = FileWriter(file)
                writer.write(csvContent)
                writer.close()

                // Note: To use FileProvider, we would need to configure it in AndroidManifest.xml.
                // Since we might not have FileProvider configured, let's just use Intent.ACTION_SEND with text.
                // Alternatively, we can use ACTION_CREATE_DOCUMENT to let the user save it anywhere.
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error exporting CSV")
            }
        }
    }

    fun exportToCsvText(): String {
        val list = vocabularyList.value
        return buildString {
            append("Kanji,Hiragana,Romaji,Meaning\n")
            list.forEach { vocab ->
                val kanji = vocab.kanji.replace(",", " ")
                val hiragana = vocab.hiragana.replace(",", " ")
                val romaji = vocab.romaji.replace(",", " ")
                val meaning = vocab.meaning.replace(",", " ")
                append("$kanji,$hiragana,$romaji,$meaning\n")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }
}

class UserVocabularyViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserVocabularyViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = UserVocabularyRepository(database.userVocabularyDao())
            @Suppress("UNCHECKED_CAST")
            return UserVocabularyViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
