package com.group02.mobile.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    viewModel: ReviewViewModel,
    onNavigateBack: () -> Unit
) {
    val wordsToReview by viewModel.wordsToReview.collectAsState()
    val masteredCount by viewModel.masteredCount.collectAsState()
    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchDailyPlan()
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
                        text = "Ôn tập hàng ngày",
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

            if (wordsToReview.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Tuyệt vời!\nBạn đã hoàn thành mục tiêu hôm nay.",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = NotoSansJP
                    )
                }
            } else {
                val currentWord = wordsToReview.getOrNull(currentIndex)
                if (currentWord != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Còn lại: ${wordsToReview.size - currentIndex}  •  ⭐ Thuộc lòng: $masteredCount",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontFamily = NotoSansJP
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val rotationY by animateFloatAsState(
                            targetValue = if (isFlipped) 180f else 0f,
                            animationSpec = tween(durationMillis = 400),
                            label = "flipAnimation"
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .aspectRatio(4f / 5f)
                                .clickable { isFlipped = !isFlipped }
                                .graphicsLayer {
                                    this.rotationY = rotationY
                                    cameraDistance = 12f * density
                                }
                                .border(2.dp, CardBorder, RoundedCornerShape(24.dp)),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = InkDark)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (rotationY <= 90f) {
                                    // Front
                                    Text(
                                        text = currentWord.word.ifEmpty { currentWord.hiragana },
                                        fontSize = 64.sp,
                                        color = SakuraPink,
                                        fontFamily = NotoSansJP,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    // Back
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.graphicsLayer {
                                            this.rotationY = 180f
                                        }.padding(16.dp)
                                    ) {
                                        if (currentWord.word.isNotEmpty()) {
                                            Text(
                                                text = currentWord.hiragana,
                                                fontSize = 24.sp,
                                                color = TextSecondary,
                                                fontFamily = NotoSansJP
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                        Text(
                                            text = currentWord.romaji,
                                            fontSize = 20.sp,
                                            color = TextHint,
                                            fontFamily = NotoSansJP
                                        )
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text(
                                            text = currentWord.meanings,
                                            fontSize = 24.sp,
                                            color = TextPrimary,
                                            fontFamily = NotoSansJP,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        if (isFlipped) {
                            // "Mastered" button - full width, separate row
                            Button(
                                onClick = {
                                    viewModel.markAsMastered(currentWord)
                                    isFlipped = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                            ) {
                                Text("⭐ Thuộc lòng rồi - Không nhắc nữa", color = InkDark, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            // 4 SM-2 rating buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = { 
                                        viewModel.processReview(currentWord, 0)
                                        isFlipped = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                                ) {
                                    Text("Quên", color = TextPrimary)
                                }
                                Button(
                                    onClick = { 
                                        viewModel.processReview(currentWord, 3)
                                        isFlipped = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = WarningOrange)
                                ) {
                                    Text("Khó", color = InkDark)
                                }
                                Button(
                                    onClick = { 
                                        viewModel.processReview(currentWord, 4)
                                        isFlipped = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                ) {
                                    Text("Tốt", color = TextPrimary)
                                }
                                Button(
                                    onClick = { 
                                        viewModel.processReview(currentWord, 5)
                                        isFlipped = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SakuraPink)
                                ) {
                                    Text("Dễ", color = InkDark)
                                }
                            }
                        } else {
                            Text(
                                text = "Tap vào thẻ để xem đáp án",
                                color = TextHint,
                                fontSize = 14.sp,
                                fontFamily = NotoSansJP
                            )
                        }
                    }
                }
            }
        }
    }
}
