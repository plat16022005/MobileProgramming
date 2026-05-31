package com.group02.mobile.ui.screens.alphabet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.data.model.alphabet.KanaType
import com.group02.mobile.data.repository.KanaRepository
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.KanaViewModel
import com.group02.mobile.viewmodel.SharedPracticeViewModel

val SuccessGreen = Color(0xFF4CAF50)

@Composable
fun KanaQuizRoute(
    rowId: String,
    kanaType: KanaType,
    viewModel: KanaViewModel,
    onNavigateBack: () -> Unit
) {
    val row = KanaRepository.getRowById(rowId) ?: return
    val question by viewModel.quizQuestion.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val totalAnswered by viewModel.quizTotalAnswered.collectAsState()
    
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isAnswerCorrect by remember { mutableStateOf<Boolean?>(null) }
    var showResult by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.generateQuizQuestion(row, kanaType)
    }

    QuizContent(
        title = "Trắc Nghiệm — ${row.rowNameDisplay}",
        score = score,
        totalAnswered = totalAnswered,
        totalQuestions = row.characters.size,
        questionDisplay = question?.displayChar ?: "",
        questionPrompt = "Ký tự này đọc là gì?",
        options = question?.options ?: emptyList(),
        correctAnswer = question?.correctAnswer ?: "",
        selectedAnswer = selectedAnswer,
        isAnswerCorrect = isAnswerCorrect,
        showResult = showResult,
        onOptionSelected = { option ->
            if (selectedAnswer == null) {
                selectedAnswer = option
                isAnswerCorrect = viewModel.submitAnswer(option)
            }
        },
        onNextQuestion = {
            selectedAnswer = null
            isAnswerCorrect = null
            if (totalAnswered >= row.characters.size) {
                showResult = true
            } else {
                viewModel.generateQuizQuestion(row, kanaType)
            }
        },
        onRetry = {
            showResult = false
            viewModel.selectRow(row) // reset score
            viewModel.generateQuizQuestion(row, kanaType)
        },
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun CustomQuizRoute(
    viewModel: SharedPracticeViewModel,
    onNavigateBack: () -> Unit
) {
    val question by viewModel.quizQuestion.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val totalAnswered by viewModel.quizTotalAnswered.collectAsState()
    val vocabularies by viewModel.vocabularies.collectAsState()

    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isAnswerCorrect by remember { mutableStateOf<Boolean?>(null) }
    var showResult by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (vocabularies.isNotEmpty() && question == null) {
            viewModel.generateQuizQuestion()
        }
    }

    if (vocabularies.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(InkBlack), contentAlignment = Alignment.Center) {
            Text("Không có từ vựng nào.", color = TextPrimary)
        }
        return
    }

    QuizContent(
        title = "Trắc Nghiệm Cá Nhân",
        score = score,
        totalAnswered = totalAnswered,
        totalQuestions = vocabularies.size,
        questionDisplay = question?.displayChar ?: "",
        questionPrompt = "Nghĩa của từ này là gì?",
        options = question?.options ?: emptyList(),
        correctAnswer = question?.correctAnswer ?: "",
        selectedAnswer = selectedAnswer,
        isAnswerCorrect = isAnswerCorrect,
        showResult = showResult,
        onOptionSelected = { option ->
            if (selectedAnswer == null) {
                selectedAnswer = option
                isAnswerCorrect = viewModel.submitAnswer(option)
            }
        },
        onNextQuestion = {
            selectedAnswer = null
            isAnswerCorrect = null
            if (totalAnswered >= vocabularies.size) {
                showResult = true
            } else {
                viewModel.generateQuizQuestion()
            }
        },
        onRetry = {
            showResult = false
            viewModel.resetQuiz()
            viewModel.generateQuizQuestion()
        },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizContent(
    title: String,
    score: Int,
    totalAnswered: Int,
    totalQuestions: Int,
    questionDisplay: String,
    questionPrompt: String,
    options: List<String>,
    correctAnswer: String,
    selectedAnswer: String?,
    isAnswerCorrect: Boolean?,
    showResult: Boolean,
    onOptionSelected: (String) -> Unit,
    onNextQuestion: () -> Unit,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InkBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        fontFamily = NotoSansJP,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = InkDark
                )
            )

            if (showResult) {
                // Result Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Kết quả",
                        fontSize = 32.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = NotoSansJP
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "$score / $totalQuestions",
                        fontSize = 48.sp,
                        color = SakuraPink,
                        fontWeight = FontWeight.Bold,
                        fontFamily = NotoSansJP
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = NihonRedLight)
                    ) {
                        Text("Thử Lại")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SakuraPink)
                    ) {
                        Text("Về Menu")
                    }
                }
            } else if (options.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Đúng: $score",
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Đã làm: $totalAnswered / $totalQuestions",
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = questionDisplay,
                        fontSize = if (questionDisplay.length > 2) 72.sp else 120.sp,
                        color = SakuraPink,
                        fontFamily = NotoSansJP,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = questionPrompt,
                        fontSize = 18.sp,
                        color = TextSecondary,
                        fontFamily = NotoSansJP,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (options.size > 0) {
                                OptionButton(
                                    text = options[0],
                                    selectedAnswer = selectedAnswer,
                                    correctAnswer = correctAnswer,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onOptionSelected(options[0]) }
                                )
                            }
                            if (options.size > 1) {
                                OptionButton(
                                    text = options[1],
                                    selectedAnswer = selectedAnswer,
                                    correctAnswer = correctAnswer,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onOptionSelected(options[1]) }
                                )
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (options.size > 2) {
                                OptionButton(
                                    text = options[2],
                                    selectedAnswer = selectedAnswer,
                                    correctAnswer = correctAnswer,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onOptionSelected(options[2]) }
                                )
                            }
                            if (options.size > 3) {
                                OptionButton(
                                    text = options[3],
                                    selectedAnswer = selectedAnswer,
                                    correctAnswer = correctAnswer,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onOptionSelected(options[3]) }
                                )
                            }
                        }
                    }

                    if (selectedAnswer != null) {
                        Spacer(modifier = Modifier.height(32.dp))
                        val msg = if (isAnswerCorrect == true) "Đúng rồi! $questionDisplay = $correctAnswer" else "Sai rồi! $questionDisplay = $correctAnswer"
                        Text(
                            text = msg,
                            color = if (isAnswerCorrect == true) SuccessGreen else NihonRedLight,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = NotoSansJP,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNextQuestion,
                            colors = ButtonDefaults.buttonColors(containerColor = SakuraPink)
                        ) {
                            Text(if (totalAnswered >= totalQuestions) "Xem kết quả" else "Câu Tiếp Theo", color = InkBlack)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptionButton(
    text: String,
    selectedAnswer: String?,
    correctAnswer: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        selectedAnswer == null -> InkDark
        text == correctAnswer -> SuccessGreen
        text == selectedAnswer -> NihonRedLight
        else -> InkDark
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        contentPadding = PaddingValues(8.dp)
    ) {
        Text(
            text = text,
            fontSize = if (text.length > 8) 16.sp else 24.sp,
            color = TextPrimary,
            fontFamily = NotoSansJP,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
