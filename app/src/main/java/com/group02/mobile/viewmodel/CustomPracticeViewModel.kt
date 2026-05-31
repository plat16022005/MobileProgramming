package com.group02.mobile.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group02.mobile.data.local.AppDatabase
import com.group02.mobile.data.model.vocabulary.PracticeMode
import com.group02.mobile.data.model.vocabulary.PracticeSession
import com.group02.mobile.data.model.vocabulary.UserVocabulary
import com.group02.mobile.data.repository.UserVocabularyRepository
import com.group02.mobile.utils.CsvParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.util.UUID

class CustomPracticeViewModel(
    application: Application,
    private val repository: UserVocabularyRepository
) : AndroidViewModel(application) {

    private val _selectedVocabularies = MutableStateFlow<List<UserVocabulary>>(emptyList())
    val selectedVocabularies: StateFlow<List<UserVocabulary>> = _selectedVocabularies.asStateFlow()

    private val _practiceMode = MutableStateFlow<PracticeMode?>(null)
    val practiceMode: StateFlow<PracticeMode?> = _practiceMode.asStateFlow()

    private val _sessionReady = MutableStateFlow(false)
    val sessionReady: StateFlow<Boolean> = _sessionReady.asStateFlow()

    private val _importError = MutableStateFlow<String?>(null)
    val importError: StateFlow<String?> = _importError.asStateFlow()

    fun loadAllMyVocabularies() {
        viewModelScope.launch {
            repository.getAllVocabularies().collect { list ->
                _selectedVocabularies.value = list
            }
        }
    }

    // Since we need getVocabulariesByIds, we should update UserVocabularyDao to support this or filter locally
    fun loadFromMyVocabulary(ids: List<String>) {
        viewModelScope.launch {
            // Using a one-shot query. For now, since repository returns Flow, we can just collect first or map.
            // Better approach: Dao should have `getVocabulariesByIds` (I added it to DAO earlier)
            val db = AppDatabase.getDatabase(getApplication())
            val list = db.userVocabularyDao().getVocabulariesByIds(ids)
            val currentList = _selectedVocabularies.value.toMutableList()
            currentList.addAll(list.filter { new -> currentList.none { it.id == new.id } })
            _selectedVocabularies.value = currentList
        }
    }

    fun importFromCsv(uri: Uri) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val reader = InputStreamReader(inputStream)
                    val content = reader.readText()
                    reader.close()
                    inputStream.close()
                    
                    val vocabularies = CsvParser.parse(content)
                    if (vocabularies.isNotEmpty()) {
                        val currentList = _selectedVocabularies.value.toMutableList()
                        currentList.addAll(vocabularies)
                        _selectedVocabularies.value = currentList
                        _importError.value = null
                    } else {
                        _importError.value = "No valid vocabulary found in CSV"
                    }
                } else {
                    _importError.value = "Could not open file"
                }
            } catch (e: Exception) {
                _importError.value = e.message ?: "Error importing CSV"
            }
        }
    }

    fun addManualVocabulary(item: UserVocabulary) {
        val currentList = _selectedVocabularies.value.toMutableList()
        currentList.add(item)
        _selectedVocabularies.value = currentList
    }

    fun removeVocabulary(id: String) {
        val currentList = _selectedVocabularies.value.toMutableList()
        currentList.removeAll { it.id == id }
        _selectedVocabularies.value = currentList
    }

    fun clearSelection() {
        _selectedVocabularies.value = emptyList()
        _practiceMode.value = null
        _sessionReady.value = false
    }

    fun setPracticeMode(mode: PracticeMode) {
        _practiceMode.value = mode
        checkSessionReady()
    }

    private fun checkSessionReady() {
        _sessionReady.value = _selectedVocabularies.value.isNotEmpty() && _practiceMode.value != null
    }

    fun startSession(): PracticeSession {
        return PracticeSession(
            id = UUID.randomUUID().toString(),
            name = "Practice ${System.currentTimeMillis()}",
            vocabularyIds = emptyList(), // For simplicity, we just pass the objects as temporary
            temporaryVocabularies = _selectedVocabularies.value,
            mode = _practiceMode.value ?: PracticeMode.STUDY
        )
    }

    fun clearError() {
        _importError.value = null
    }
}

class CustomPracticeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomPracticeViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = UserVocabularyRepository(database.userVocabularyDao())
            @Suppress("UNCHECKED_CAST")
            return CustomPracticeViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
