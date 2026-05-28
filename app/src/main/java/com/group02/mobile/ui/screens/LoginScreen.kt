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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Entrance animation
    val alphaAnim = remember { Animatable(0f) }
    val slideAnim = remember { Animatable(40f) }
    LaunchedEffect(Unit) {
        viewModel.clearError()
        alphaAnim.animateTo(1f, animationSpec = tween(600))
        slideAnim.animateTo(0f, animationSpec = tween(600, easing = FastOutSlowInEasing))
    }

    // Navigate on success
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn && uiState.isSuccess) onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InkBlack)
    ) {
        // Decorative background blobs
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .blur(80.dp)
                .background(
                    NihonRedDark.copy(alpha = 0.4f),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .blur(80.dp)
                .background(
                    SakuraPinkDark.copy(alpha = 0.15f),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 28.dp)
                .offset(y = slideAnim.value.dp)
                .alpha(alphaAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.radialGradient(colors = listOf(NihonRedLight, NihonRedDark)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "日",
                    fontSize = 38.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = NotoSansJP
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Nihonlish",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontFamily = NotoSansJP,
                letterSpacing = 1.sp
            )
            Text(
                text = "ようこそ  •  Xin chào",
                fontSize = 13.sp,
                color = SakuraPink,
                fontFamily = NotoSansJP,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = CardSurface,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    Text(
                        text = "Đăng nhập",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = NotoSansJP
                    )
                    Text(
                        text = "Tiếp tục hành trình học tiếng Nhật",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontFamily = NotoSansJP
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email field
                    NihonTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        placeholder = "email@example.com",
                        leadingIcon = Icons.Outlined.Email,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password field
                    NihonTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Mật khẩu",
                        placeholder = "••••••••",
                        leadingIcon = Icons.Outlined.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onPasswordToggle = { passwordVisible = !passwordVisible },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.signInWithEmail(email, password)
                            }
                        )
                    )

                    // Forgot password
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(onClick = onNavigateToForgotPassword) {
                            Text(
                                text = "Quên mật khẩu?",
                                color = NihonRedLight,
                                fontSize = 12.sp,
                                fontFamily = NotoSansJP
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Error message
                    AnimatedVisibility(visible = uiState.errorMessage != null) {
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
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Login button
                    NihonPrimaryButton(
                        text = "Đăng nhập",
                        isLoading = uiState.isLoading,
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.clearError()
                            viewModel.signInWithEmail(email, password)
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Divider
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Divider(modifier = Modifier.weight(1f), color = CardBorder)
                        Text(
                            text = "  hoặc  ",
                            color = TextHint,
                            fontSize = 12.sp,
                            fontFamily = NotoSansJP
                        )
                        Divider(modifier = Modifier.weight(1f), color = CardBorder)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Google Sign-In button
                    GoogleSignInButton(
                        isLoading = uiState.isLoading,
                        onClick = {
                            viewModel.clearError()
                            // Replace with your actual Web Client ID from Firebase console
                            viewModel.signInWithGoogle(
                                context = context,
                                webClientId = "301401160677-l4t3qa2151shi0o3odff7e9k9lvgkr3d.apps.googleusercontent.com"
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register link
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Chưa có tài khoản?",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontFamily = NotoSansJP
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Đăng ký ngay",
                        color = NihonRedLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = NotoSansJP
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
