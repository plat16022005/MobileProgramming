package com.group02.mobile.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }

    val alphaAnim = remember { Animatable(0f) }
    val slideAnim = remember { Animatable(40f) }
    LaunchedEffect(Unit) {
        viewModel.clearError()
        alphaAnim.animateTo(1f, animationSpec = tween(600))
        slideAnim.animateTo(0f, animationSpec = tween(600, easing = FastOutSlowInEasing))
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            emailSent = true
            viewModel.resetSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InkBlack)
    ) {
        // Background blobs
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-60).dp)
                .blur(90.dp)
                .background(NihonRedDark.copy(alpha = 0.3f), shape = CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .offset(y = slideAnim.value.dp)
                .alpha(alphaAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Back button + header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateToLogin) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Lock icon with animation
            val pulseAnim by rememberInfiniteTransition(label = "pulse").animateFloat(
                initialValue = 0.95f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse"
            )

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .scale(pulseAnim)
                    .background(
                        Brush.radialGradient(colors = listOf(NihonRedLight, NihonRedDark)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (emailSent) Icons.Filled.CheckCircle else Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            AnimatedContent(
                targetState = emailSent,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith
                            fadeOut(animationSpec = tween(400))
                },
                label = "content"
            ) { sent ->
                if (sent) {
                    // Success state
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Email đã gửi! 🌸",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = NotoSansJP
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Chúng tôi đã gửi link đặt lại mật khẩu đến\n$email",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            fontFamily = NotoSansJP,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "メールを確認してください",
                            fontSize = 12.sp,
                            color = SakuraPink,
                            fontFamily = NotoSansJP,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = CardSurface,
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "💡 Mẹo",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoldAccent,
                                    fontFamily = NotoSansJP
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Kiểm tra hộp thư spam nếu bạn không thấy email trong vòng vài phút.",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontFamily = NotoSansJP,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                NihonPrimaryButton(
                                    text = "Quay lại đăng nhập",
                                    isLoading = false,
                                    onClick = onNavigateToLogin
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                TextButton(onClick = {
                                    emailSent = false
                                    viewModel.clearError()
                                }) {
                                    Text(
                                        text = "Gửi lại email",
                                        color = TextSecondary,
                                        fontSize = 13.sp,
                                        fontFamily = NotoSansJP
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Input state
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Quên mật khẩu?",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = NotoSansJP
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nhập email để nhận link đặt lại mật khẩu",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            fontFamily = NotoSansJP,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "パスワードをリセット",
                            fontSize = 12.sp,
                            color = SakuraPink,
                            fontFamily = NotoSansJP,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(36.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            color = CardSurface,
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                NihonTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = "Email",
                                    placeholder = "email@example.com",
                                    leadingIcon = Icons.Outlined.Email,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            focusManager.clearFocus()
                                            viewModel.sendPasswordReset(email)
                                        }
                                    )
                                )

                                AnimatedVisibility(visible = uiState.errorMessage != null) {
                                    Column {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = ErrorRed.copy(alpha = 0.12f),
                                            border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Filled.Warning,
                                                    contentDescription = null,
                                                    tint = ErrorRed,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(
                                                    text = uiState.errorMessage ?: "",
                                                    color = ErrorRed,
                                                    fontSize = 12.sp,
                                                    fontFamily = NotoSansJP
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                NihonPrimaryButton(
                                    text = "Gửi email",
                                    isLoading = uiState.isLoading,
                                    onClick = {
                                        focusManager.clearFocus()
                                        viewModel.clearError()
                                        viewModel.sendPasswordReset(email)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        TextButton(onClick = onNavigateToLogin) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = NihonRedLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Quay lại đăng nhập",
                                    color = NihonRedLight,
                                    fontSize = 13.sp,
                                    fontFamily = NotoSansJP
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
