package com.group02.mobile.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group02.mobile.data.local.AppDatabase
import com.group02.mobile.data.model.vocabulary.UserVocabulary
import com.group02.mobile.data.repository.UserVocabularyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID

sealed class UiState {
    object Loading : UiState()
    object Success : UiState()
    data class Error(val message: String) : UiState()
}

class UserVocabularyViewModel : ViewModel() {

    // Repository will be initialized via context since we don't have Hilt
    private var repository: UserVocabularyRepository? = null

    private val _vocabularyList = MutableStateFlow<List<UserVocabulary>>(emptyList())
    val vocabularyList: StateFlow<List<UserVocabulary>> = _vocabularyList.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Success)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredList: StateFlow<List<UserVocabulary>> = combine(
        _vocabularyList, _searchQuery
    ) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter {
                it.kanji.contains(query, ignoreCase = true) ||
                it.hiragana.contains(query, ignoreCase = true) ||
                it.romaji.contains(query, ignoreCase = true) ||
                it.meaning.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun initialize(context: Context) {
        if (repository == null) {
            val dao = AppDatabase.getDatabase(context).userVocabularyDao()
            repository = UserVocabularyRepository(dao)
            
            viewModelScope.launch {
                _uiState.value = UiState.Loading
                repository?.getAllVocabularies()?.collect { list ->
                    _vocabularyList.value = list
                    _uiState.value = UiState.Success
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun addVocabulary(kanji: String, hiragana: String, romaji: String, meaning: String) {
        val newItem = UserVocabulary(
            kanji = kanji,
            hiragana = hiragana,
            romaji = romaji,
            meaning = meaning
        )
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository?.addVocabulary(newItem)
            if (result?.isFailure == true) {
                _uiState.value = UiState.Error("Lỗi khi thêm từ mới")
            } else {
                _uiState.value = UiState.Success
            }
        }
    }

    fun updateVocabulary(item: UserVocabulary) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository?.updateVocabulary(item)
            if (result?.isFailure == true) {
                _uiState.value = UiState.Error("Lỗi khi cập nhật từ")
            } else {
                _uiState.value = UiState.Success
            }
        }
    }

    fun deleteVocabulary(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository?.deleteVocabulary(id)
            if (result?.isFailure == true) {
                _uiState.value = UiState.Error("Lỗi khi xoá từ")
            } else {
                _uiState.value = UiState.Success
            }
        }
    }

    fun importFromCsv(uri: Uri, context: Context, onResult: (Int, Int) -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val csvContent = reader.readText()
                    reader.close()

                    val result = repository?.importFromCsv(csvContent)
                    if (result != null && result.isSuccess) {
                        val importedCount = result.getOrNull() ?: 0
                        onResult(importedCount, 0) // success
                        _uiState.value = UiState.Success
                    } else {
                        _uiState.value = UiState.Error("Lỗi khi lưu dữ liệu import")
                        onResult(0, 1)
                    }
                } else {
                    _uiState.value = UiState.Error("Không thể đọc file")
                    onResult(0, 1)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Lỗi khi import file CSV: ${e.message}")
                onResult(0, 1)
            }
        }
    }

    fun resetUiState() {
        _uiState.value = UiState.Success
    }
}
