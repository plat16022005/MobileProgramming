package com.group02.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.data.model.srs.SrsData
import com.group02.mobile.ui.theme.*
import com.group02.mobile.utils.TtsManager
import com.group02.mobile.viewmodel.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLessonScreen(
    viewModel: ReviewViewModel,
    onNavigateBack: () -> Unit
) {
    val dailyWords by viewModel.dailyNewWords.collectAsState()
    val learnedIds by viewModel.learnedDailyWordIds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchDailyNewWords()
    }

    val learnedCount = learnedIds.size
    val totalCount = dailyWords.size

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
            // ── Top Bar ────────────────────────────────────────────────────────
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Từ mới hôm nay",
                            fontFamily = NotoSansJP,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 20.sp
                        )
                        if (totalCount > 0) {
                            Text(
                                text = "$learnedCount / $totalCount đã thuộc",
                                color = GoldAccent,
                                fontSize = 13.sp,
                                fontFamily = NotoSansJP
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = InkDark
                )
            )

            // ── Progress Bar ───────────────────────────────────────────────────
            if (totalCount > 0) {
                LinearProgressIndicator(
                    progress = { learnedCount.toFloat() / totalCount },
                    modifier = Modifier.fillMaxWidth(),
                    color = GoldAccent,
                    trackColor = InkLight
                )
            }

            // ── Content ────────────────────────────────────────────────────────
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GoldAccent)
                    }
                }

                dailyWords.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Hôm nay chưa có từ mới.\nBạn đã hoàn thành tất cả! 🎉",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = NotoSansJP
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(dailyWords) { _, wordItem ->
                            val isLearned = wordItem.wordId in learnedIds
                            DailyWordItem(
                                word = wordItem,
                                isLearned = isLearned,
                                onToggleLearned = {
                                    if (!isLearned) {
                                        viewModel.markWordAsLearned(wordItem)
                                    }
                                },
                                onSpeak = {
                                    val speakText = wordItem.hiragana.ifEmpty { wordItem.word }
                                    TtsManager.speak(context, speakText, "")
                                }
                            )
                        }

                        // Completion banner
                        if (learnedCount == totalCount && totalCount > 0) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = InkDark),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("🎉", fontSize = 40.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Tuyệt vời! Bạn đã học xong\n$totalCount từ hôm nay!",
                                            color = GoldAccent,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            fontFamily = NotoSansJP
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Quay lại ngày mai để ôn lại nhé.",
                                            color = TextSecondary,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center,
                                            fontFamily = NotoSansJP
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = onNavigateBack,
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Hoàn thành", color = InkDark, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyWordItem(
    word: SrsData,
    isLearned: Boolean,
    onToggleLearned: () -> Unit,
    onSpeak: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = InkDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ── Header: word info + action buttons ────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Word info column
                Column(modifier = Modifier.weight(1f)) {
                    val displayWord = word.word.ifEmpty { word.hiragana }
                    Text(
                        text = displayWord,
                        fontSize = 28.sp,
                        color = TextPrimary,
                        fontFamily = NotoSansJP,
                        fontWeight = FontWeight.Bold
                    )
                    if (word.hiragana.isNotEmpty() && word.hiragana != word.word) {
                        Text(
                            text = word.hiragana,
                            fontSize = 16.sp,
                            color = SakuraPink,
                            fontFamily = NotoSansJP
                        )
                    }
                    if (word.romaji.isNotEmpty()) {
                        Text(
                            text = word.romaji,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            fontFamily = NotoSansJP
                        )
                    }
                }

                // Action buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // "Đã thuộc" toggle
                    IconButton(
                        onClick = onToggleLearned,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .background(
                                if (isLearned) GoldAccent.copy(alpha = 0.15f) else InkBlack,
                                RoundedCornerShape(50)
                            )
                    ) {
                        Icon(
                            imageVector = if (isLearned) Icons.Filled.CheckCircle
                                          else Icons.Outlined.CheckCircle,
                            contentDescription = "Đã thuộc",
                            tint = if (isLearned) GoldAccent else TextHint
                        )
                    }

                    // TTS button
                    IconButton(
                        onClick = onSpeak,
                        modifier = Modifier
                            .background(InkBlack, RoundedCornerShape(50))
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Phát âm",
                            tint = SakuraPink
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(modifier = Modifier.height(12.dp))

            // ── Vietnamese meaning ────────────────────────────────────────────
            Text(
                text = word.meanings,
                fontSize = 16.sp,
                color = TextSecondary,
                fontFamily = NotoSansJP
            )

            // ── Learned badge ─────────────────────────────────────────────────
            if (isLearned) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "✅ Đã thêm vào hệ thống ôn tập",
                    color = GoldAccent,
                    fontSize = 13.sp,
                    fontFamily = NotoSansJP
                )
            }
        }
    }
}
