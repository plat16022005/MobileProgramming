package com.group02.mobile.ui.screens.grammar

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.data.model.grammar.Grammar
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.GrammarViewModel

// Reuse level accent colors
private val levelAccentColors = mapOf(
    "N5" to Color(0xFF52B788),
    "N4" to Color(0xFF5BA4CF),
    "N3" to Color(0xFFD4845A),
    "N2" to Color(0xFF9B72CF),
    "N1" to Color(0xFFE57373)
)

private val levelGradients = mapOf(
    "N5" to listOf(Color(0xFF1B4332), Color(0xFF2D6A4F)),
    "N4" to listOf(Color(0xFF1A3A5C), Color(0xFF2C5F8A)),
    "N3" to listOf(Color(0xFF3D2B1F), Color(0xFF6B4226)),
    "N2" to listOf(Color(0xFF2C1654), Color(0xFF4A2E8A)),
    "N1" to listOf(Color(0xFF4A0E1A), Color(0xFF7F0000))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarLevelScreen(
    level: String,
    viewModel: GrammarViewModel,
    onNavigateBack: () -> Unit,
    onGrammarClick: (Grammar) -> Unit
) {
    val grammarList by viewModel.grammarList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var searchActive by remember { mutableStateOf(false) }

    val accentColor = levelAccentColors[level] ?: SakuraPink
    val gradient = levelGradients[level] ?: listOf(InkDark, InkBlack)

    // Load grammar for this level
    LaunchedEffect(level) {
        viewModel.selectLevel(level)
    }

    val filteredList = remember(grammarList, searchQuery) {
        if (searchQuery.isBlank()) grammarList
        else grammarList.filter { g ->
            g.title.contains(searchQuery, ignoreCase = true) ||
                    g.shortExplanation.contains(searchQuery, ignoreCase = true)
        }
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
            // ── Top Bar ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(colors = gradient)
                    )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    CenterAlignedTopAppBar(
                        title = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "JLPT $level",
                                    fontFamily = NotoSansJP,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "Ngữ pháp",
                                    fontSize = 12.sp,
                                    color = accentColor
                                )
                            }
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
                        actions = {
                            IconButton(onClick = { searchActive = !searchActive }) {
                                Icon(
                                    imageVector = if (searchActive) Icons.Default.Close else Icons.Default.Search,
                                    contentDescription = "Tìm kiếm",
                                    tint = accentColor
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )

                    // Stats row
                    AnimatedVisibility(visible = !searchActive) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatChip(
                                label = "${grammarList.size}",
                                sublabel = "Tổng ngữ pháp",
                                color = accentColor
                            )
                            if (searchQuery.isNotBlank()) {
                                StatChip(
                                    label = "${filteredList.size}",
                                    sublabel = "Kết quả tìm",
                                    color = GoldAccent
                                )
                            }
                        }
                    }

                    // Search bar
                    AnimatedVisibility(
                        visible = searchActive,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            placeholder = { Text("Tìm kiếm ngữ pháp...", color = TextHint) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = accentColor.copy(alpha = 0.4f),
                                cursorColor = accentColor,
                                focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                                unfocusedContainerColor = Color.Black.copy(alpha = 0.3f)
                            ),
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = accentColor
                                )
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // ── Content ───────────────────────────────────────────
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = accentColor)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Đang tải ngữ pháp $level...",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                filteredList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🔍", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (searchQuery.isBlank()) "Không có dữ liệu"
                                else "Không tìm thấy \"$searchQuery\"",
                                color = TextSecondary,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp, end = 16.dp,
                            top = 12.dp, bottom = 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(filteredList) { index, grammar ->
                            GrammarLevelItem(
                                index = index + 1,
                                grammar = grammar,
                                accentColor = accentColor,
                                onClick = { onGrammarClick(grammar) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, sublabel: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.18f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = sublabel,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun GrammarLevelItem(
    index: Int,
    grammar: Grammar,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = InkDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, end = 16.dp, top = 0.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Index badge
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(48.dp)
                    .background(
                        color = accentColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$index",
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 14.dp)
            ) {
                Text(
                    text = grammar.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    fontFamily = NotoSansJP,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = grammar.shortExplanation,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
