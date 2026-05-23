package com.group02.mobile.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isLoggedIn: Boolean,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // Animations
    val logoScale by rememberInfiniteTransition(label = "logo").animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alphaAnim = remember { Animatable(0f) }
    val slideAnim = remember { Animatable(60f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(800))
        slideAnim.animateTo(0f, animationSpec = tween(800, easing = FastOutSlowInEasing))
        delay(1800)
        if (isLoggedIn) onNavigateToHome() else onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF3D0000),
                        Color(0xFF1A0000),
                        InkBlack
                    ),
                    center = Offset(0.5f, 0.4f),
                    radius = 1200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative circles (sakura glow)
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.Center)
                .alpha(0.08f)
                .scale(logoScale)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NihonRedLight, Color.Transparent)
                    ),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )

        Column(
            modifier = Modifier
                .offset(y = slideAnim.value.dp)
                .alpha(alphaAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App icon / logo area
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(logoScale)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(NihonRedLight, NihonRedDark)
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Kanji character for Japanese
                Text(
                    text = "日",
                    fontSize = 52.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = NotoSansJP
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App name
            Text(
                text = "Nihonlish",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontFamily = NotoSansJP,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Japanese subtitle
            Text(
                text = "日本語を学ぼう",
                fontSize = 18.sp,
                color = SakuraPink,
                fontFamily = NotoSansJP,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Học tiếng Nhật thông minh",
                fontSize = 13.sp,
                color = TextHint,
                fontFamily = NotoSansJP,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Loading dots
            LoadingDots()
        }

        // Bottom tagline
        Text(
            text = "✦ Nihonlish v1.0 ✦",
            fontSize = 11.sp,
            color = TextHint.copy(alpha = 0.6f),
            fontFamily = NotoSansJP,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(alphaAnim.value)
        )
    }
}

@Composable
private fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$index"
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .alpha(alpha)
                    .background(NihonRedLight, shape = androidx.compose.foundation.shape.CircleShape)
            )
        }
    }
}
