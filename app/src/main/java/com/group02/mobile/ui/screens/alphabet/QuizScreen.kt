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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.data.model.alphabet.KanaType
import com.group02.mobile.data.repository.KanaRepository
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.KanaViewModel
import kotlinx.coroutines.delay

val SuccessGreen = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
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
                        text = "Trắc Nghiệm — ${row.rowNameDisplay}",
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
                        text = "$score / ${row.characters.size}",
                        fontSize = 48.sp,
                        color = SakuraPink,
                        fontWeight = FontWeight.Bold,
                        fontFamily = NotoSansJP
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            showResult = false
                            viewModel.selectRow(row) // reset score
                            viewModel.generateQuizQuestion(row, kanaType)
                        },
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
            } else if (question != null) {
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
                            text = "Đã làm: $totalAnswered / ${row.characters.size}",
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = question!!.displayChar,
                        fontSize = 120.sp,
                        color = SakuraPink,
                        fontFamily = NotoSansJP,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Ký tự này đọc là gì?",
                        fontSize = 18.sp,
                        color = TextSecondary,
                        fontFamily = NotoSansJP
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
                            OptionButton(
                                text = question!!.options[0],
                                selectedAnswer = selectedAnswer,
                                correctAnswer = question!!.correctAnswer,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (selectedAnswer == null) {
                                        selectedAnswer = question!!.options[0]
                                        isAnswerCorrect = viewModel.submitAnswer(question!!.options[0])
                                    }
                                }
                            )
                            OptionButton(
                                text = question!!.options[1],
                                selectedAnswer = selectedAnswer,
                                correctAnswer = question!!.correctAnswer,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (selectedAnswer == null) {
                                        selectedAnswer = question!!.options[1]
                                        isAnswerCorrect = viewModel.submitAnswer(question!!.options[1])
                                    }
                                }
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OptionButton(
                                text = question!!.options[2],
                                selectedAnswer = selectedAnswer,
                                correctAnswer = question!!.correctAnswer,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (selectedAnswer == null) {
                                        selectedAnswer = question!!.options[2]
                                        isAnswerCorrect = viewModel.submitAnswer(question!!.options[2])
                                    }
                                }
                            )
                            OptionButton(
                                text = question!!.options[3],
                                selectedAnswer = selectedAnswer,
                                correctAnswer = question!!.correctAnswer,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (selectedAnswer == null) {
                                        selectedAnswer = question!!.options[3]
                                        isAnswerCorrect = viewModel.submitAnswer(question!!.options[3])
                                    }
                                }
                            )
                        }
                    }

                    if (selectedAnswer != null) {
                        Spacer(modifier = Modifier.height(32.dp))
                        val msg = if (isAnswerCorrect == true) "Đúng rồi! ${question!!.displayChar} = ${question!!.correctAnswer}" else "Sai rồi! ${question!!.displayChar} = ${question!!.correctAnswer}"
                        Text(
                            text = msg,
                            color = if (isAnswerCorrect == true) SuccessGreen else NihonRedLight,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = NotoSansJP
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                selectedAnswer = null
                                isAnswerCorrect = null
                                if (totalAnswered >= row.characters.size) {
                                    showResult = true
                                } else {
                                    viewModel.generateQuizQuestion(row, kanaType)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SakuraPink)
                        ) {
                            Text(if (totalAnswered >= row.characters.size) "Xem kết quả" else "Câu Tiếp Theo", color = InkBlack)
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
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            color = TextPrimary,
            fontFamily = NotoSansJP,
            fontWeight = FontWeight.Bold
        )
    }
}
