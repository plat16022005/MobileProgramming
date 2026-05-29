package com.group02.mobile.ui.screens.alphabet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.KanjiViewModel
import com.group02.mobile.data.model.alphabet.KanjiDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanjiDetailScreen(
    kanji: String,
    viewModel: KanjiViewModel,
    onNavigateBack: () -> Unit
) {
    val kanjiDetail by viewModel.kanjiDetail.collectAsState()
    val isLoading by viewModel.isDetailLoading.collectAsState()
    val error by viewModel.detailError.collectAsState()

    LaunchedEffect(kanji) {
        viewModel.fetchKanjiDetail(kanji)
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
                        text = "Chi tiết Kanji",
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

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NihonRedLight)
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Lỗi: $error", color = NihonRedLight)
                }
            } else if (kanjiDetail != null) {
                val detail = kanjiDetail!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Big Kanji Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = InkDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = detail.kanji,
                                fontSize = 120.sp,
                                fontFamily = NotoSansJP,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Detail items
                    KanjiDetailSection(title = "Nghĩa", items = detail.meanings)
                    KanjiDetailSection(title = "Âm Kun", items = detail.kunReadings)
                    KanjiDetailSection(title = "Âm On", items = detail.onReadings)

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        detail.grade?.let { InfoBadge("Cấp độ", it.toString()) }
                        detail.strokeCount?.let { InfoBadge("Số nét", it.toString()) }
                        detail.jlpt?.let { InfoBadge("JLPT", "N$it") }
                    }
                }
            }
        }
    }
}

@Composable
fun KanjiDetailSection(title: String, items: List<String>) {
    if (items.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = NihonRedLight,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = NotoSansJP
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = InkDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Text(
                            text = item,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoBadge(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = NihonRedLight.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(1.dp, NihonRedLight)
        ) {
            Text(
                text = value,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
