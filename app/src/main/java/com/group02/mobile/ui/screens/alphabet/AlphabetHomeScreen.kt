package com.group02.mobile.ui.screens.alphabet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.data.model.alphabet.KanaType
import com.group02.mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetHomeScreen(
    onNavigateBack: () -> Unit,
    onSelectKanaType: (KanaType) -> Unit
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
                        text = "Bảng Chữ Cái",
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
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Hiragana Card
                KanaTypeCard(
                    title = "ひらがな",
                    subtitle = "Hiragana",
                    description = "Bảng chữ mềm, dùng cho các từ thuần Nhật.",
                    iconText = "あ",
                    onClick = { onSelectKanaType(KanaType.HIRAGANA) }
                )

                // Katakana Card
                KanaTypeCard(
                    title = "カタカナ",
                    subtitle = "Katakana",
                    description = "Bảng chữ cứng, dùng cho các từ mượn tiếng nước ngoài.",
                    iconText = "ア",
                    onClick = { onSelectKanaType(KanaType.KATAKANA) }
                )
            }
        }
    }
}

@Composable
fun KanaTypeCard(
    title: String,
    subtitle: String,
    description: String,
    iconText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = InkDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(NihonRedLight.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .border(1.dp, NihonRedLight, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconText,
                    fontSize = 48.sp,
                    color = SakuraPink,
                    fontFamily = NotoSansJP
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 24.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = NotoSansJP
                )
                Text(
                    text = subtitle,
                    fontSize = 16.sp,
                    color = SakuraPink,
                    fontWeight = FontWeight.Medium,
                    fontFamily = NotoSansJP
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontFamily = NotoSansJP,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
