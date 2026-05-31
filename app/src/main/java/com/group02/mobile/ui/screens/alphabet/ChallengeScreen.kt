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
import kotlinx.coroutines.delay

@Composable
fun KanaChallengeRoute(
    rowId: String,
    kanaType: KanaType,
    viewModel: KanaViewModel,
    onNavigateBack: () -> Unit
) {
    val row = KanaRepository.getRowById(rowId) ?: return
    val question by viewModel.quizQuestion.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val totalAnswered by viewModel.quizTotalAnswered.collectAsState()
    val timeLeft by viewModel.challengeTimeLeft.collectAsState()
    val isRunning by viewModel.isChallengeRunning.collectAsState()

    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var flashCorrect by remember { mutableStateOf<Boolean?>(null) }

    DisposableEffect(Unit) {
        viewModel.startChallenge(row, kanaType)
        onDispose {
            viewModel.stopChallenge()
        }
    }
    
    // Auto next question logic
    LaunchedEffect(flashCorrect) {
        if (flashCorrect != null) {
            delay(200) // Flash color for 200ms
            selectedAnswer = null
            flashCorrect = null
            viewModel.nextChallengeQuestion(row, kanaType)
        }
    }

    ChallengeContent(
        title = "Thử Thách ⏱️",
        score = score,
        totalAnswered = totalAnswered,
        timeLeft = timeLeft,
        isRunning = isRunning,
        questionDisplay = question?.displayChar ?: "",
        questionPrompt = "Chọn cách đọc đúng:",
        options = question?.options ?: emptyList(),
        correctAnswer = question?.correctAnswer ?: "",
        selectedAnswer = selectedAnswer,
        onOptionSelected = { option ->
            if (selectedAnswer == null) {
                selectedAnswer = option
                flashCorrect = viewModel.submitAnswer(option)
            }
        },
        onRetry = { viewModel.startChallenge(row, kanaType) },
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun CustomChallengeRoute(
    viewModel: SharedPracticeViewModel,
    onNavigateBack: () -> Unit
) {
    val question by viewModel.quizQuestion.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val totalAnswered by viewModel.quizTotalAnswered.collectAsState()
    val timeLeft by viewModel.challengeTimeLeft.collectAsState()
    val isRunning by viewModel.isChallengeRunning.collectAsState()
    val vocabularies by viewModel.vocabularies.collectAsState()

    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var flashCorrect by remember { mutableStateOf<Boolean?>(null) }

    DisposableEffect(Unit) {
        if (vocabularies.isNotEmpty()) {
            viewModel.startChallenge()
        }
        onDispose {
            viewModel.stopChallenge()
        }
    }

    LaunchedEffect(flashCorrect) {
        if (flashCorrect != null) {
            delay(200)
            selectedAnswer = null
            flashCorrect = null
            viewModel.nextChallengeQuestion()
        }
    }

    if (vocabularies.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(InkBlack), contentAlignment = Alignment.Center) {
            Text("Không có từ vựng nào.", color = TextPrimary)
        }
        return
    }

    ChallengeContent(
        title = "Thử Thách Cá Nhân ⏱️",
        score = score,
        totalAnswered = totalAnswered,
        timeLeft = timeLeft,
        isRunning = isRunning,
        questionDisplay = question?.displayChar ?: "",
        questionPrompt = "Chọn nghĩa đúng:",
        options = question?.options ?: emptyList(),
        correctAnswer = question?.correctAnswer ?: "",
        selectedAnswer = selectedAnswer,
        onOptionSelected = { option ->
            if (selectedAnswer == null) {
                selectedAnswer = option
                flashCorrect = viewModel.submitAnswer(option)
            }
        },
        onRetry = { viewModel.startChallenge() },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeContent(
    title: String,
    score: Int,
    totalAnswered: Int,
    timeLeft: Int,
    isRunning: Boolean,
    questionDisplay: String,
    questionPrompt: String,
    options: List<String>,
    correctAnswer: String,
    selectedAnswer: String?,
    onOptionSelected: (String) -> Unit,
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

            if (!isRunning && timeLeft == 0) {
                // Result
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Thời Gian Kết Thúc!", fontSize = 28.sp, color = NihonRedLight, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Câu đúng: $score", fontSize = 20.sp, color = SuccessGreen)
                    Text("Câu sai: ${totalAnswered - score}", fontSize = 20.sp, color = NihonRedLight)
                    val accuracy = if (totalAnswered > 0) (score * 100) / totalAnswered else 0
                    Text("Độ chính xác: $accuracy%", fontSize = 20.sp, color = SakuraPink)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = SakuraPink)
                    ) {
                        Text("Chơi Lại", color = InkBlack)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
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
                    Text(
                        text = "$timeLeft",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (timeLeft <= 10) NihonRedLight else TextPrimary
                    )
                    LinearProgressIndicator(
                        progress = { timeLeft / 60f },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = if (timeLeft <= 10) NihonRedLight else SakuraPink,
                        trackColor = InkDark
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = questionDisplay,
                        fontSize = if (questionDisplay.length > 2) 72.sp else 100.sp,
                        color = SakuraPink,
                        fontFamily = NotoSansJP,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(questionPrompt, color = TextSecondary, fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                            if (options.size > 0) {
                                ChallengeOptionButton(
                                    text = options[0], selectedAnswer = selectedAnswer, correctAnswer = correctAnswer, modifier = Modifier.weight(1f)
                                ) { onOptionSelected(options[0]) }
                            }
                            if (options.size > 1) {
                                ChallengeOptionButton(
                                    text = options[1], selectedAnswer = selectedAnswer, correctAnswer = correctAnswer, modifier = Modifier.weight(1f)
                                ) { onOptionSelected(options[1]) }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                            if (options.size > 2) {
                                ChallengeOptionButton(
                                    text = options[2], selectedAnswer = selectedAnswer, correctAnswer = correctAnswer, modifier = Modifier.weight(1f)
                                ) { onOptionSelected(options[2]) }
                            }
                            if (options.size > 3) {
                                ChallengeOptionButton(
                                    text = options[3], selectedAnswer = selectedAnswer, correctAnswer = correctAnswer, modifier = Modifier.weight(1f)
                                ) { onOptionSelected(options[3]) }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("✓ $score", color = SuccessGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("✗ ${totalAnswered - score}", color = NihonRedLight, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeOptionButton(
    text: String,
    selectedAnswer: String?,
    correctAnswer: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        selectedAnswer == null -> InkDark
        text == correctAnswer && selectedAnswer != null -> SuccessGreen
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
