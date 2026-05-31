package com.group02.mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group02.mobile.data.model.grammar.Grammar
import com.group02.mobile.data.repository.GrammarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GrammarViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GrammarRepository(application)

    // List of grammar for the currently loaded level (used by GrammarLevelScreen)
    private val _grammarList = MutableStateFlow<List<Grammar>>(emptyList())
    val grammarList: StateFlow<List<Grammar>> = _grammarList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedLevel = MutableStateFlow("N5")
    val selectedLevel: StateFlow<String> = _selectedLevel.asStateFlow()

    // Count of grammar items per level — shown on the level-picker screen
    private val _grammarCountByLevel = MutableStateFlow<Map<String, Int>>(emptyMap())
    val grammarCountByLevel: StateFlow<Map<String, Int>> = _grammarCountByLevel.asStateFlow()

    private val _isCountLoading = MutableStateFlow(true)
    val isCountLoading: StateFlow<Boolean> = _isCountLoading.asStateFlow()

    val levels = listOf("N5", "N4", "N3", "N2", "N1")

    init {
        loadAllCounts()
    }

    /** Load grammar count for every level in background (used on the home/level picker screen). */
    private fun loadAllCounts() {
        viewModelScope.launch {
            _isCountLoading.value = true
            val counts = mutableMapOf<String, Int>()
            for (level in levels) {
                val list = repository.getGrammarList(level)
                counts[level] = list.size
            }
            _grammarCountByLevel.value = counts
            _isCountLoading.value = false
        }
    }

    /** Load the full grammar list for a specific level. */
    fun selectLevel(level: String) {
        if (_selectedLevel.value == level && _grammarList.value.isNotEmpty()) return
        _selectedLevel.value = level
        viewModelScope.launch {
            _isLoading.value = true
            _grammarList.value = repository.getGrammarList(level)
            _isLoading.value = false
        }
    }
}
