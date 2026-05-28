package com.group02.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group02.mobile.data.model.alphabet.KanaCharacter
import com.group02.mobile.data.model.alphabet.KanaRow
import com.group02.mobile.data.model.alphabet.KanaType
import com.group02.mobile.data.repository.KanaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizQuestion(
    val character: KanaCharacter,
    val displayChar: String,
    val correctAnswer: String,
    val options: List<String>
)

class KanaViewModel : ViewModel() {

    private val _selectedKanaType = MutableStateFlow(KanaType.HIRAGANA)
    val selectedKanaType: StateFlow<KanaType> = _selectedKanaType.asStateFlow()

    private val _selectedRow = MutableStateFlow<KanaRow?>(null)
    val selectedRow: StateFlow<KanaRow?> = _selectedRow.asStateFlow()

    // Flash Card State
    private val _currentCardIndex = MutableStateFlow(0)
    val currentCardIndex: StateFlow<Int> = _currentCardIndex.asStateFlow()

    private val _isCardFlipped = MutableStateFlow(false)
    val isCardFlipped: StateFlow<Boolean> = _isCardFlipped.asStateFlow()

    // Quiz & Challenge State
    private val _quizQuestion = MutableStateFlow<QuizQuestion?>(null)
    val quizQuestion: StateFlow<QuizQuestion?> = _quizQuestion.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _quizTotalAnswered = MutableStateFlow(0)
    val quizTotalAnswered: StateFlow<Int> = _quizTotalAnswered.asStateFlow()

    private val _challengeTimeLeft = MutableStateFlow(0)
    val challengeTimeLeft: StateFlow<Int> = _challengeTimeLeft.asStateFlow()

    private val _isChallengeRunning = MutableStateFlow(false)
    val isChallengeRunning: StateFlow<Boolean> = _isChallengeRunning.asStateFlow()
    
    private var challengeJob: Job? = null

    // Writing Practice State
    private val _currentWritingCharIndex = MutableStateFlow(0)
    val currentWritingCharIndex: StateFlow<Int> = _currentWritingCharIndex.asStateFlow()

    fun selectKanaType(type: KanaType) {
        _selectedKanaType.value = type
    }

    fun selectRow(row: KanaRow) {
        _selectedRow.value = row
        resetCards()
        resetWritingPractice()
        _quizScore.value = 0
        _quizTotalAnswered.value = 0
    }

    fun nextCard(size: Int) {
        if (_currentCardIndex.value < size - 1) {
            _currentCardIndex.value++
            _isCardFlipped.value = false
        }
    }

    fun prevCard() {
        if (_currentCardIndex.value > 0) {
            _currentCardIndex.value--
            _isCardFlipped.value = false
        }
    }

    fun flipCard() {
        _isCardFlipped.value = !_isCardFlipped.value
    }

    fun resetCards() {
        _currentCardIndex.value = 0
        _isCardFlipped.value = false
    }

    fun generateQuizQuestion(row: KanaRow, kanaType: KanaType) {
        if (row.characters.isEmpty()) return
        val correctChar = row.characters.random()
        val displayChar = KanaRepository.getCharacterDisplay(correctChar, kanaType)
        
        val allChars = KanaRepository.getAllRows().flatMap { it.characters }
        val incorrectChars = allChars.filter { it.romaji.isNotEmpty() && it.romaji != correctChar.romaji }
            .shuffled()
            .take(3)
            
        val options = (incorrectChars.map { it.romaji } + correctChar.romaji).shuffled()
        
        _quizQuestion.value = QuizQuestion(
            character = correctChar,
            displayChar = displayChar,
            correctAnswer = correctChar.romaji,
            options = options
        )
    }

    fun submitAnswer(selectedAnswer: String): Boolean {
        val question = _quizQuestion.value ?: return false
        val isCorrect = selectedAnswer == question.correctAnswer
        if (isCorrect) {
            _quizScore.value++
        }
        _quizTotalAnswered.value++
        return isCorrect
    }

    fun startChallenge(row: KanaRow, kanaType: KanaType, durationSeconds: Int = 60) {
        _quizScore.value = 0
        _quizTotalAnswered.value = 0
        _challengeTimeLeft.value = durationSeconds
        _isChallengeRunning.value = true
        generateQuizQuestion(row, kanaType)
        
        challengeJob?.cancel()
        challengeJob = viewModelScope.launch {
            while (_challengeTimeLeft.value > 0) {
                delay(1000)
                _challengeTimeLeft.value--
            }
            _isChallengeRunning.value = false
        }
    }
    
    fun nextChallengeQuestion(row: KanaRow, kanaType: KanaType) {
        if (_isChallengeRunning.value) {
            generateQuizQuestion(row, kanaType)
        }
    }

    fun stopChallenge() {
        challengeJob?.cancel()
        _isChallengeRunning.value = false
    }

    fun nextWritingChar(size: Int) {
        if (_currentWritingCharIndex.value < size - 1) {
            _currentWritingCharIndex.value++
        }
    }
    
    fun prevWritingChar() {
        if (_currentWritingCharIndex.value > 0) {
            _currentWritingCharIndex.value--
        }
    }

    fun resetWritingPractice() {
        _currentWritingCharIndex.value = 0
    }
}
