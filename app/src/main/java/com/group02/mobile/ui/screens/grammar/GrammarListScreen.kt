package com.group02.mobile.ui.screens.grammar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.GrammarViewModel

// Per-level visual config
private data class LevelConfig(
    val label: String,
    val kanji: String,
    val description: String,
    val gradientStart: Color,
    val gradientEnd: Color,
    val accentColor: Color
)

private val levelConfigs = mapOf(
    "N5" to LevelConfig(
        label = "N5", kanji = "初", description = "Người mới bắt đầu",
        gradientStart = Color(0xFF1B4332), gradientEnd = Color(0xFF2D6A4F),
        accentColor = Color(0xFF52B788)
    ),
    "N4" to LevelConfig(
        label = "N4", kanji = "基", description = "Cơ bản sơ cấp",
        gradientStart = Color(0xFF1A3A5C), gradientEnd = Color(0xFF2C5F8A),
        accentColor = Color(0xFF5BA4CF)
    ),
    "N3" to LevelConfig(
        label = "N3", kanji = "中", description = "Trung cấp",
        gradientStart = Color(0xFF3D2B1F), gradientEnd = Color(0xFF6B4226),
        accentColor = Color(0xFFD4845A)
    ),
    "N2" to LevelConfig(
        label = "N2", kanji = "上", description = "Nâng cao",
        gradientStart = Color(0xFF2C1654), gradientEnd = Color(0xFF4A2E8A),
        accentColor = Color(0xFF9B72CF)
    ),
    "N1" to LevelConfig(
        label = "N1", kanji = "極", description = "Chuyên nghiệp",
        gradientStart = Color(0xFF4A0E1A), gradientEnd = Color(0xFF7F0000),
        accentColor = Color(0xFFE57373)
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarListScreen(
    viewModel: GrammarViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLevel: (String) -> Unit,
    // Legacy param kept for NavGraph compatibility (unused here)
    onGrammarClick: (com.group02.mobile.data.model.grammar.Grammar) -> Unit = {}
) {
    val countByLevel by viewModel.grammarCountByLevel.collectAsState()
    val isCountLoading by viewModel.isCountLoading.collectAsState()

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
            // ── Top App Bar ───────────────────────────────────────
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Ngữ Pháp JLPT",
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

            // ── Header ────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(InkDark, InkBlack)
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "文法",
                    fontSize = 56.sp,
                    fontFamily = NotoSansJP,
                    fontWeight = FontWeight.Bold,
                    color = SakuraPink
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Chọn cấp độ để bắt đầu học ngữ pháp",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // ── Level Cards ───────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                viewModel.levels.forEach { level ->
                    val config = levelConfigs[level]!!
                    val count = countByLevel[level]
                    LevelCard(
                        config = config,
                        count = count,
                        isLoading = isCountLoading && count == null,
                        onClick = { onNavigateToLevel(level) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun LevelCard(
    config: LevelConfig,
    count: Int?,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(120),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                pressed = true
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(config.gradientStart, config.gradientEnd)
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Kanji Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(config.accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = config.kanji,
                        fontSize = 32.sp,
                        fontFamily = NotoSansJP,
                        fontWeight = FontWeight.Bold,
                        color = config.accentColor
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Label + description + count
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "JLPT ${config.label}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        fontFamily = NotoSansJP
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = config.description,
                        fontSize = 13.sp,
                        color = config.accentColor,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Count badge
                    if (isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .width(80.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = config.accentColor,
                            trackColor = config.accentColor.copy(alpha = 0.2f)
                        )
                    } else if (count != null) {
                        Surface(
                            color = config.accentColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "$count ngữ pháp",
                                fontSize = 12.sp,
                                color = config.accentColor,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Arrow
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = config.accentColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
