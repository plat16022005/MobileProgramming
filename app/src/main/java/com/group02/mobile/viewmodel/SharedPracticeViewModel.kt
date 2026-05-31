package com.group02.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group02.mobile.data.model.vocabulary.PracticeMode
import com.group02.mobile.data.model.vocabulary.PracticeSession
import com.group02.mobile.data.model.vocabulary.UserVocabulary
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CustomQuizQuestion(
    val vocabulary: UserVocabulary,
    val displayWord: String,
    val correctAnswer: String,
    val options: List<String>
)

class SharedPracticeViewModel : ViewModel() {

    private val _practiceSession = MutableStateFlow<PracticeSession?>(null)
    val practiceSession: StateFlow<PracticeSession?> = _practiceSession.asStateFlow()

    private val _vocabularies = MutableStateFlow<List<UserVocabulary>>(emptyList())
    val vocabularies: StateFlow<List<UserVocabulary>> = _vocabularies.asStateFlow()

    // Flash Card State
    private val _currentCardIndex = MutableStateFlow(0)
    val currentCardIndex: StateFlow<Int> = _currentCardIndex.asStateFlow()

    private val _isCardFlipped = MutableStateFlow(false)
    val isCardFlipped: StateFlow<Boolean> = _isCardFlipped.asStateFlow()

    // Quiz & Challenge State
    private val _quizQuestion = MutableStateFlow<CustomQuizQuestion?>(null)
    val quizQuestion: StateFlow<CustomQuizQuestion?> = _quizQuestion.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _quizTotalAnswered = MutableStateFlow(0)
    val quizTotalAnswered: StateFlow<Int> = _quizTotalAnswered.asStateFlow()

    private val _challengeTimeLeft = MutableStateFlow(0)
    val challengeTimeLeft: StateFlow<Int> = _challengeTimeLeft.asStateFlow()

    private val _isChallengeRunning = MutableStateFlow(false)
    val isChallengeRunning: StateFlow<Boolean> = _isChallengeRunning.asStateFlow()

    private var challengeJob: Job? = null

    fun loadSession(session: PracticeSession) {
        _practiceSession.value = session
        _vocabularies.value = session.temporaryVocabularies.shuffled()
        resetCards()
        _quizScore.value = 0
        _quizTotalAnswered.value = 0
    }

    // Flashcard functions
    fun nextCard() {
        if (_currentCardIndex.value < _vocabularies.value.size - 1) {
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

    // Quiz functions
    fun generateQuizQuestion() {
        val list = _vocabularies.value
        if (list.isEmpty()) return
        
        val correctWord = list.random()
        val displayWord = if (correctWord.kanji.isNotEmpty()) "${correctWord.kanji} (${correctWord.hiragana})" else correctWord.hiragana
        
        val incorrectWords = list.filter { it.meaning != correctWord.meaning }
            .shuffled()
            .take(3)
            
        // If not enough words, fill with empty or duplicates, but realistically we need at least 4 words
        val options = (incorrectWords.map { it.meaning } + correctWord.meaning).shuffled()
        
        _quizQuestion.value = CustomQuizQuestion(
            vocabulary = correctWord,
            displayWord = displayWord,
            correctAnswer = correctWord.meaning,
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

    // Challenge functions
    fun startChallenge(durationSeconds: Int = 60) {
        _quizScore.value = 0
        _quizTotalAnswered.value = 0
        _challengeTimeLeft.value = durationSeconds
        _isChallengeRunning.value = true
        generateQuizQuestion()
        
        challengeJob?.cancel()
        challengeJob = viewModelScope.launch {
            while (_challengeTimeLeft.value > 0) {
                delay(1000)
                _challengeTimeLeft.value--
            }
            _isChallengeRunning.value = false
        }
    }
    
    fun nextChallengeQuestion() {
        if (_isChallengeRunning.value) {
            generateQuizQuestion()
        }
    }

    fun stopChallenge() {
        challengeJob?.cancel()
        _isChallengeRunning.value = false
    }
}
