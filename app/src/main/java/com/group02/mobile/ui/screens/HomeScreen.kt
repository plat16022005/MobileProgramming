package com.group02.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.ui.theme.*

@Composable
fun HomeScreen(
    onSignOut: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InkBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "日",
                fontSize = 72.sp,
                color = NihonRedLight,
                fontFamily = NotoSansJP,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Chào mừng đến Nihonlish!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontFamily = NotoSansJP
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "日本語を楽しく学ぼう",
                fontSize = 16.sp,
                color = SakuraPink,
                fontFamily = NotoSansJP,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "(Màn hình chính sẽ được xây dựng tiếp)",
                fontSize = 12.sp,
                color = TextHint,
                fontFamily = NotoSansJP,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedButton(
                onClick = onSignOut,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NihonRedLight)
            ) {
                Text(
                    text = "Đăng xuất",
                    fontFamily = NotoSansJP,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
