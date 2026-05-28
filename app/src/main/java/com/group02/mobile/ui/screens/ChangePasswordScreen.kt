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
fun ChangePasswordScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var passwordChanged by remember { mutableStateOf(false) }

    val alphaAnim = remember { Animatable(0f) }
    val slideAnim = remember { Animatable(40f) }
    LaunchedEffect(Unit) {
        viewModel.clearError()
        alphaAnim.animateTo(1f, animationSpec = tween(600))
        slideAnim.animateTo(0f, animationSpec = tween(600, easing = FastOutSlowInEasing))
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            passwordChanged = true
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
                .alpha(alphaAnim.value)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Back button + header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    imageVector = if (passwordChanged) Icons.Filled.CheckCircle else Icons.Outlined.LockReset,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            AnimatedContent(
                targetState = passwordChanged,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith
                            fadeOut(animationSpec = tween(400))
                },
                label = "content"
            ) { changed ->
                if (changed) {
                    // Success state
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Đổi mật khẩu thành công! 🌸",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = NotoSansJP
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Mật khẩu của bạn đã được cập nhật.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            fontFamily = NotoSansJP,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "パスワードが変更されました",
                            fontSize = 12.sp,
                            color = SakuraPink,
                            fontFamily = NotoSansJP,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        NihonPrimaryButton(
                            text = "Quay lại hồ sơ",
                            isLoading = false,
                            onClick = onNavigateBack
                        )
                    }
                } else {
                    // Input state
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Đổi mật khẩu",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = NotoSansJP
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Vui lòng nhập mật khẩu cũ và mới",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            fontFamily = NotoSansJP,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "パスワードの変更",
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
                                var currentPasswordVisible by remember { mutableStateOf(false) }
                                NihonTextField(
                                    value = currentPassword,
                                    onValueChange = { currentPassword = it },
                                    label = "Mật khẩu hiện tại",
                                    placeholder = "••••••••",
                                    leadingIcon = Icons.Outlined.Lock,
                                    isPassword = true,
                                    passwordVisible = currentPasswordVisible,
                                    onPasswordToggle = { currentPasswordVisible = !currentPasswordVisible },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Next
                                    )
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                var newPasswordVisible by remember { mutableStateOf(false) }
                                NihonTextField(
                                    value = newPassword,
                                    onValueChange = { newPassword = it },
                                    label = "Mật khẩu mới",
                                    placeholder = "••••••••",
                                    leadingIcon = Icons.Outlined.Lock,
                                    isPassword = true,
                                    passwordVisible = newPasswordVisible,
                                    onPasswordToggle = { newPasswordVisible = !newPasswordVisible },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Next
                                    )
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                var confirmPasswordVisible by remember { mutableStateOf(false) }
                                NihonTextField(
                                    value = confirmPassword,
                                    onValueChange = { confirmPassword = it },
                                    label = "Xác nhận mật khẩu mới",
                                    placeholder = "••••••••",
                                    leadingIcon = Icons.Outlined.Lock,
                                    isPassword = true,
                                    passwordVisible = confirmPasswordVisible,
                                    onPasswordToggle = { confirmPasswordVisible = !confirmPasswordVisible },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            focusManager.clearFocus()
                                            viewModel.changePassword(currentPassword, newPassword, confirmPassword)
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
                                    text = "Đổi mật khẩu",
                                    isLoading = uiState.isLoading,
                                    onClick = {
                                        focusManager.clearFocus()
                                        viewModel.clearError()
                                        viewModel.changePassword(currentPassword, newPassword, confirmPassword)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}
