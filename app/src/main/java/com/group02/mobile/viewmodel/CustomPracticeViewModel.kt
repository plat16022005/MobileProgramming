package com.group02.mobile.viewmodel

import androidx.lifecycle.ViewModel
import com.group02.mobile.data.model.vocabulary.PracticeMode
import com.group02.mobile.data.model.vocabulary.PracticeSession
import com.group02.mobile.data.model.vocabulary.UserVocabulary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CustomPracticeViewModel : ViewModel() {

    private val _selectedVocabularies = MutableStateFlow<List<UserVocabulary>>(emptyList())
    val selectedVocabularies: StateFlow<List<UserVocabulary>> = _selectedVocabularies.asStateFlow()

    private val _practiceMode = MutableStateFlow<PracticeMode?>(null)
    val practiceMode: StateFlow<PracticeMode?> = _practiceMode.asStateFlow()

    private val _currentSession = MutableStateFlow<PracticeSession?>(null)
    val currentSession: StateFlow<PracticeSession?> = _currentSession.asStateFlow()

    // Flag for UI to navigate
    private val _startPractice = MutableStateFlow(false)
    val startPractice: StateFlow<Boolean> = _startPractice.asStateFlow()

    fun setSelectedVocabularies(list: List<UserVocabulary>) {
        _selectedVocabularies.value = list
    }

    fun addManualVocabulary(item: UserVocabulary) {
        val currentList = _selectedVocabularies.value.toMutableList()
        currentList.add(item)
        _selectedVocabularies.value = currentList
    }

    fun removeVocabulary(id: String) {
        _selectedVocabularies.value = _selectedVocabularies.value.filter { it.id != id }
    }

    fun clearSelection() {
        _selectedVocabularies.value = emptyList()
    }

    fun setPracticeMode(mode: PracticeMode) {
        _practiceMode.value = mode
    }

    fun startSession() {
        val mode = _practiceMode.value ?: return
        val list = _selectedVocabularies.value
        if (list.isEmpty()) return

        _currentSession.value = PracticeSession(
            name = "Luyện tập tuỳ chỉnh",
            selectedVocabularies = list,
            mode = mode
        )
        _startPractice.value = true
    }

    fun onPracticeStarted() {
        _startPractice.value = false
    }
}
