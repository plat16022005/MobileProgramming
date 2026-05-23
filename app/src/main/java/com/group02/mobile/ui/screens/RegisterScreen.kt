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
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val alphaAnim = remember { Animatable(0f) }
    val slideAnim = remember { Animatable(40f) }
    LaunchedEffect(Unit) {
        viewModel.clearError()
        alphaAnim.animateTo(1f, animationSpec = tween(600))
        slideAnim.animateTo(0f, animationSpec = tween(600, easing = FastOutSlowInEasing))
    }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn && uiState.isSuccess) onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InkBlack)
    ) {
        // Background blobs
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .blur(80.dp)
                .background(SakuraPinkDark.copy(alpha = 0.2f), shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 60.dp)
                .blur(80.dp)
                .background(NihonRedDark.copy(alpha = 0.35f), shape = CircleShape)
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
            Spacer(modifier = Modifier.height(60.dp))

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateToLogin) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Tạo tài khoản",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = NotoSansJP
                    )
                    Text(
                        text = "はじめましょう • Bắt đầu thôi!",
                        fontSize = 12.sp,
                        color = SakuraPink,
                        fontFamily = NotoSansJP,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Level selector (decorative)
            LevelSelectorRow()

            Spacer(modifier = Modifier.height(20.dp))

            // Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = CardSurface,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // Display Name
                    NihonTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = "Tên hiển thị",
                        placeholder = "Tên của bạn",
                        leadingIcon = Icons.Outlined.Person,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Email
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

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password
                    NihonTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Mật khẩu",
                        placeholder = "Tối thiểu 6 ký tự",
                        leadingIcon = Icons.Outlined.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onPasswordToggle = { passwordVisible = !passwordVisible },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    // Password strength indicator
                    if (password.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        PasswordStrengthBar(password = password)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Confirm Password
                    NihonTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Xác nhận mật khẩu",
                        placeholder = "Nhập lại mật khẩu",
                        leadingIcon = Icons.Outlined.Lock,
                        isPassword = true,
                        passwordVisible = confirmPasswordVisible,
                        onPasswordToggle = { confirmPasswordVisible = !confirmPasswordVisible },
                        isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Terms checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = agreedToTerms,
                            onCheckedChange = { agreedToTerms = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = NihonRedLight,
                                uncheckedColor = TextHint,
                                checkmarkColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tôi đồng ý với ",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontFamily = NotoSansJP
                        )
                        Text(
                            text = "Điều khoản sử dụng",
                            color = NihonRedLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = NotoSansJP,
                            modifier = Modifier.clickable { }
                        )
                    }

                    // Error message
                    AnimatedVisibility(visible = uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Register button
                    NihonPrimaryButton(
                        text = "Đăng ký",
                        isLoading = uiState.isLoading,
                        enabled = agreedToTerms,
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.clearError()
                            viewModel.registerWithEmail(email, password, displayName, confirmPassword)
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Divider(modifier = Modifier.weight(1f), color = CardBorder)
                        Text("  hoặc  ", color = TextHint, fontSize = 12.sp, fontFamily = NotoSansJP)
                        Divider(modifier = Modifier.weight(1f), color = CardBorder)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GoogleSignInButton(
                        isLoading = uiState.isLoading,
                        onClick = {
                            viewModel.clearError()
                            viewModel.signInWithGoogle(
                                context = context,
                                webClientId = "301401160677-l4t3qa2151shi0o3odff7e9k9lvgkr3d.apps.googleusercontent.com"
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Đã có tài khoản?",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontFamily = NotoSansJP
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Đăng nhập",
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

@Composable
private fun LevelSelectorRow() {
    val levels = listOf(
        "N5" to "Sơ cấp",
        "N4" to "Cơ bản",
        "N3" to "Trung cấp",
        "N2" to "Cao cấp",
        "N1" to "Thành thạo"
    )
    var selectedLevel by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Trình độ của bạn",
            color = TextSecondary,
            fontSize = 12.sp,
            fontFamily = NotoSansJP
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            levels.forEachIndexed { index, (level, _) ->
                val isSelected = index == selectedLevel
                Surface(
                    onClick = { selectedLevel = index },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) NihonRedDark else InkMedium,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) NihonRedLight else CardBorder
                    )
                ) {
                    Text(
                        text = level,
                        color = if (isSelected) Color.White else TextHint,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = NotoSansJP,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun PasswordStrengthBar(password: String) {
    val strength = when {
        password.length < 6 -> 0
        password.length < 8 -> 1
        password.length >= 8 && (password.any { it.isUpperCase() } || password.any { it.isDigit() }) -> 2
        password.length >= 10 && password.any { it.isUpperCase() } && password.any { it.isDigit() } -> 3
        else -> 2
    }
    val (color, label) = when (strength) {
        0 -> ErrorRed to "Yếu"
        1 -> WarningOrange to "Trung bình"
        2 -> SuccessGreen to "Mạnh"
        else -> GoldAccent to "Rất mạnh"
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(4) { i ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (i <= strength) color else InkLight)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Độ mạnh: $label",
            color = color,
            fontSize = 10.sp,
            fontFamily = NotoSansJP
        )
    }
}
