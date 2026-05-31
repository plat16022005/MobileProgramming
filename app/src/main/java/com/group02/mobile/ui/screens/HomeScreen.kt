package com.group02.mobile.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.AuthViewModel

@Composable
fun HomeScreen(
    viewModel: AuthViewModel,
    onNavigateToSetupProfile: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAlphabet: () -> Unit,
    onNavigateToKanjiList: () -> Unit,
    onNavigateToDictionary: () -> Unit,
    onNavigateToNotificationSetting: () -> Unit,
    onSignOut: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userAccount = uiState.userAccount
    val userProfile = uiState.userProfile
    val userProgress = uiState.userProgress

    val learnedWordsCount = userProgress?.learnedWordsCount ?: 0
    val streakDays = userProgress?.streakDays ?: 0
    val accuracy = userProgress?.accuracy ?: 0.0

    var menuExpanded by remember { mutableStateOf(false) }

    // Fetch user profile when entering home
    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
        viewModel.fetchUserProgress()
    }

    // Check if profile is completed (profileCompleted lives in UserAccount)
    LaunchedEffect(userAccount) {
        if (userAccount != null && !userAccount.profileCompleted) {
            onNavigateToSetupProfile()
        }
    }

    if (uiState.isLoading || userAccount == null || userProfile == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(InkBlack),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = NihonRedLight)
        }
    } else {
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
                // --- TOP BAR WITH AVATAR ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nihonlish",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = NotoSansJP,
                        letterSpacing = 1.sp
                    )

                    // Circular Avatar Button
                    Box(modifier = Modifier.wrapContentSize()) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(InkDark)
                                .border(1.5.dp, NihonRedLight, CircleShape)
                                .clickable { menuExpanded = true },
                            contentAlignment = Alignment.Center
                        ) {
                            if (userProfile.avatarUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = userProfile.avatarUrl,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else {
                                Text(
                                    text = safeAvatar(userProfile.photoUrl),
                                    fontSize = 24.sp
                                )
                            }
                        }

                        // Dropdown Combo
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier
                                .background(InkDark)
                                .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Xem thông tin",
                                        color = TextPrimary,
                                        fontFamily = NotoSansJP
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = SakuraPink
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onNavigateToProfile()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Nhắc nhở học tập",
                                        color = NihonRedLight,
                                        fontFamily = NotoSansJP
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = SakuraPink
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onNavigateToNotificationSetting()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Đăng xuất",
                                        color = NihonRedLight,
                                        fontFamily = NotoSansJP
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = null,
                                        tint = NihonRedLight
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onSignOut()
                                }
                            )
                        }
                    }
                }

                // --- MAIN CONTENT ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = "日",
                            fontSize = 84.sp,
                            color = NihonRedLight,
                            fontFamily = NotoSansJP,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Chào mừng, ${userAccount.displayName}!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = NotoSansJP,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Mục tiêu của bạn: ${userProfile.studyLevel}",
                            fontSize = 16.sp,
                            color = SakuraPink,
                            fontFamily = NotoSansJP,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = InkDark),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProgressItem(
                                    title = "Từ đã học",
                                    value = "$learnedWordsCount"
                                )

                                ProgressItem(
                                    title = "Streak",
                                    value = "$streakDays ngày"
                                )

                                ProgressItem(
                                    title = "Accuracy",
                                    value = "${accuracy.toInt()}%"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "日本語を楽しく学ぼう",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            fontFamily = NotoSansJP,
                            letterSpacing = 2.sp
                        )

                        Text(
                            text = "日本語を楽しく学ぼう",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            fontFamily = NotoSansJP,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "(Hệ thống học tập đang được hoàn thiện)",
                            fontSize = 12.sp,
                            color = TextHint,
                            fontFamily = NotoSansJP,
                            textAlign = TextAlign.Center
                        )     
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToAlphabet() },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = InkDark),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "文字",
                                    fontSize = 32.sp,
                                    color = SakuraPink
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Hiragana & Katakana",
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = NotoSansJP
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Học bảng chữ cái Nhật Bản",
                                        color = TextSecondary,
                                        fontSize = 14.sp,
                                        fontFamily = NotoSansJP
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToKanjiList() },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = InkDark),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "漢字",
                                    fontSize = 32.sp,
                                    color = SakuraPink
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Kanji",
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = NotoSansJP
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Toàn bộ Hán tự",
                                        color = TextSecondary,
                                        fontSize = 14.sp,
                                        fontFamily = NotoSansJP
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToDictionary() },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = InkDark),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "辞書",
                                    fontSize = 32.sp,
                                    color = SakuraPink
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Từ Điển",
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = NotoSansJP
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Tìm kiếm từ vựng",
                                        color = TextSecondary,
                                        fontSize = 14.sp,
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
}
@Composable
fun ProgressItem(
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = NotoSansJP
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            color = TextSecondary,
            fontSize = 12.sp,
            fontFamily = NotoSansJP,
            textAlign = TextAlign.Center
        )
    }
}
