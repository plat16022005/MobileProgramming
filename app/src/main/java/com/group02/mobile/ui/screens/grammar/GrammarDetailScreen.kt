package com.group02.mobile.ui.screens.grammar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.data.model.grammar.Grammar
import com.group02.mobile.ui.theme.*
import com.group02.mobile.utils.TtsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarDetailScreen(
    grammar: Grammar,
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
                        text = "Chi tiết ngữ pháp",
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = grammar.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = SakuraPink,
                    fontFamily = NotoSansJP
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SectionTitle("Ý nghĩa")
                Text(
                    text = grammar.shortExplanation,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SectionTitle("Giải thích chi tiết")
                Text(
                    text = grammar.longExplanation,
                    fontSize = 15.sp,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SectionTitle("Cách dùng")
                Surface(
                    color = InkDark,
                    shape = MaterialTheme.shapes.medium,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = grammar.formation,
                        fontSize = 16.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(12.dp),
                        fontFamily = NotoSansJP
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                SectionTitle("Ví dụ")
                grammar.examples.forEach { example ->
                    ExampleItem(example)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = SakuraPink,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun ExampleItem(example: com.group02.mobile.data.model.grammar.GrammarExample) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = InkDark),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = example.jp,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                    fontFamily = NotoSansJP
                )
                IconButton(onClick = { TtsManager.speak(context, example.jp, example.romaji) }) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Nghe",
                        tint = SakuraPink
                    )
                }
            }
            Text(
                text = example.romaji,
                fontSize = 14.sp,
                color = TextSecondary,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = example.en,
                fontSize = 15.sp,
                color = TextPrimary
            )
        }
    }
}
