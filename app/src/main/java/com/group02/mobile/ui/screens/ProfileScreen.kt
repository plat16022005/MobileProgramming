package com.group02.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.data.repository.UserAccount
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onNavigateToChangePassword: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val account = uiState.userAccount
    val profile = uiState.userProfile

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InkBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .statusBarsPadding()
        ) {
            // --- TOP HEADER ---
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Hồ sơ cá nhân",
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

            if (profile == null || account == null) {
                // Profile loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NihonRedLight)
                }
            } else {
                // --- PROFILE INFO ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Big Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(NihonRedLight.copy(alpha = 0.25f), Color.Transparent)
                                ),
                                shape = CircleShape
                            )
                            .border(2.dp, NihonRedLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = safeAvatar(profile.photoUrl),
                            fontSize = 62.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Display Name
                    Text(
                        text = profile.displayName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = NotoSansJP
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Study Level Badge
                    Box(
                        modifier = Modifier
                            .background(NihonRed.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                            .border(1.dp, NihonRedLight, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Trình độ: ${profile.studyLevel}",
                            color = SakuraPink,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = NotoSansJP
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- DETAIL CARD ---
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = InkDark
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ProfileDetailItem(
                                icon = Icons.Default.Person,
                                label = "Họ và tên",
                                value = profile.displayName
                            )

                            Divider(color = CardBorder, thickness = 0.8.dp)

                            ProfileDetailItem(
                                icon = Icons.Default.Email,
                                label = "Email",
                                value = account.email
                            )

                            Divider(color = CardBorder, thickness = 0.8.dp)

                            ProfileDetailItem(
                                icon = Icons.Default.Phone,
                                label = "Số điện thoại",
                                value = profile.phoneNumber.takeIf { it.isNotEmpty() } ?: "Chưa cập nhật"
                            )

                            Divider(color = CardBorder, thickness = 0.8.dp)

                            ProfileDetailItem(
                                icon = Icons.Default.CalendarToday,
                                label = "Ngày sinh",
                                value = profile.birthDate.takeIf { it.isNotEmpty() } ?: "Chưa cập nhật"
                            )

                            Divider(color = CardBorder, thickness = 0.8.dp)

                            ProfileDetailItem(
                                icon = Icons.Default.Wc,
                                label = "Giới tính",
                                value = profile.gender.takeIf { it.isNotEmpty() } ?: "Chưa cập nhật"
                            )

                            HorizontalDivider(color = CardBorder, thickness = 0.8.dp)

                            ProfileDetailItem(
                                icon = Icons.Default.LocationOn,
                                label = "Địa chỉ",
                                value = profile.address.takeIf { it.isNotEmpty() } ?: "Chưa cập nhật"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- EDIT BUTTON ---
                    Button(
                        onClick = onNavigateToEdit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NihonRed,
                            contentColor = TextPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Chỉnh sửa thông tin",
                            fontFamily = NotoSansJP,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    if (uiState.hasPasswordProvider) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // --- CHANGE PASSWORD BUTTON ---
                        OutlinedButton(
                            onClick = onNavigateToChangePassword,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SakuraPink
                            ),
                            border = BorderStroke(1.dp, SakuraPink)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Đổi mật khẩu",
                                fontFamily = NotoSansJP,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDetailItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SakuraPink,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = NotoSansJP
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = NotoSansJP
            )
        }
    }
}
