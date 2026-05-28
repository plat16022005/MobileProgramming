package com.group02.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Image
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.os.Build
import coil.compose.AsyncImage
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.AuthViewModel
import java.util.Locale


private val GENDER_LIST = listOf("Nam", "Nữ", "Khác")

private val LEVEL_LIST = listOf(
    "Beginner",
    "N5",
    "N4",
    "N3",
    "N2",
    "N1"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupProfileScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit,
    isEditMode: Boolean
) {

    val uiState by viewModel.uiState.collectAsState()
    val isUploadingAvatar by viewModel.isUploadingAvatar.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadAndSaveAvatar(it, context) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        }
    }

    // ── Form states ───────────────────────────────────

    var displayName by remember {
        mutableStateOf(uiState.userProfile?.displayName ?: "")
    }

    var phoneNumber by remember {
        mutableStateOf(uiState.userProfile?.phoneNumber ?: "")
    }

    var birthDate by remember {
        mutableStateOf(uiState.userProfile?.birthDate ?: "")
    }

    var gender by remember {
        mutableStateOf(
            uiState.userProfile?.gender?.ifEmpty { "Nam" }
                ?: "Nam"
        )
    }

    var studyLevel by remember {
        mutableStateOf(
            uiState.userProfile?.studyLevel?.ifEmpty { "Beginner" }
                ?: "Beginner"
        )
    }

    var address by remember {
        mutableStateOf(uiState.userProfile?.address ?: "")
    }

    var selectedAvatar by remember {
        mutableStateOf(safeAvatar(uiState.userProfile?.photoUrl))
    }

    var localError by remember {
        mutableStateOf<String?>(null)
    }

    // ── Birth Date States ─────────────────────────────

    var selectedDay by remember { mutableStateOf(1) }
    var selectedMonth by remember { mutableStateOf(1) }
    var selectedYear by remember { mutableStateOf(2005) }

    val days = (1..31).toList()
    val months = (1..12).toList()
    val years = (1950..2025).reversed().toList()

    // ── Sync profile ──────────────────────────────────

    LaunchedEffect(uiState.userProfile) {

        uiState.userProfile?.let {

            displayName = it.displayName
            phoneNumber = it.phoneNumber
            birthDate = it.birthDate
            address = it.address

            if (it.gender.isNotEmpty()) {
                gender = it.gender
            }

            if (it.studyLevel.isNotEmpty()) {
                studyLevel = it.studyLevel
            }

            if (it.photoUrl.isNotEmpty()) {
                selectedAvatar = safeAvatar(it.photoUrl)
            }

            // Parse birth date
            try {

                if (it.birthDate.contains("/")) {

                    val parts = it.birthDate.split("/")

                    if (parts.size == 3) {

                        selectedDay = parts[0].toInt()
                        selectedMonth = parts[1].toInt()
                        selectedYear = parts[2].toInt()
                    }
                }

            } catch (_: Exception) {
            }
        }
    }

    // Update birthDate automatically
    birthDate = String.format(
        Locale.getDefault(),
        "%02d/%02d/%04d",
        selectedDay,
        selectedMonth,
        selectedYear
    )

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

            // ── Top Bar ─────────────────────────────────

            CenterAlignedTopAppBar(
                title = {

                    Text(
                        text = if (isEditMode)
                            "Chỉnh sửa thông tin"
                        else
                            "Thiết lập hồ sơ",

                        fontFamily = NotoSansJP,

                        fontWeight = FontWeight.Bold,

                        color = TextPrimary,

                        fontSize = 20.sp
                    )
                },

                navigationIcon = {

                    if (isEditMode) {

                        IconButton(
                            onClick = onNavigateBack
                        ) {

                            Icon(
                                imageVector =
                                    Icons.AutoMirrored.Filled.ArrowBack,

                                contentDescription = "Quay lại",

                                tint = TextPrimary
                            )
                        }
                    }
                },

                actions = {

                    if (!isEditMode) {

                        IconButton(
                            onClick = onSignOut
                        ) {

                            Icon(
                                imageVector =
                                    Icons.AutoMirrored.Filled.ExitToApp,

                                contentDescription = "Đăng xuất",

                                tint = NihonRedLight
                            )
                        }
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = InkDark
                )
            )

            // ── Main Content ────────────────────────────

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ── Avatar Preview ──────────────────────
                
                val currentProfile = uiState.userProfile
                val displayAvatarUrl = currentProfile?.avatarUrl ?: ""

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(InkDark)
                        .border(2.dp, NihonRedLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (displayAvatarUrl.isNotEmpty()) {
                        AsyncImage(
                            model = displayAvatarUrl,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Text(
                            text = selectedAvatar,
                            fontSize = 56.sp
                        )
                    }

                    if (isUploadingAvatar) {
                        CircularProgressIndicator(
                            color = SakuraPink,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Manifest.permission.READ_MEDIA_IMAGES
                        } else {
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        }
                        permissionLauncher.launch(permission)
                    },
                    modifier = Modifier.height(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary,
                        containerColor = InkDark
                    ),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Thay đổi ảnh đại diện",
                        tint = SakuraPink,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Thay đổi ảnh đại diện",
                        fontFamily = NotoSansJP,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(
                    color = CardBorder,
                    thickness = 0.8.dp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── Họ tên ──────────────────────────────

                OutlinedTextField(
                    value = displayName,

                    onValueChange = {
                        displayName = it
                    },

                    label = {
                        Text(
                            "Họ và tên",
                            color = TextSecondary
                        )
                    },

                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            null,
                            tint = SakuraPink
                        )
                    },

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(14.dp),

                    colors = nihonFieldColors(),

                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Số điện thoại ───────────────────────

                OutlinedTextField(
                    value = phoneNumber,

                    onValueChange = {
                        phoneNumber = it
                    },

                    label = {
                        Text(
                            "Số điện thoại",
                            color = TextSecondary
                        )
                    },

                    leadingIcon = {
                        Icon(
                            Icons.Default.Phone,
                            null,
                            tint = SakuraPink
                        )
                    },

                    modifier = Modifier.fillMaxWidth(),

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),

                    shape = RoundedCornerShape(14.dp),

                    colors = nihonFieldColors(),

                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Địa chỉ ─────────────────────────────

                OutlinedTextField(
                    value = address,

                    onValueChange = {
                        address = it
                    },

                    label = {
                        Text(
                            "Địa chỉ",
                            color = TextSecondary
                        )
                    },

                    leadingIcon = {
                        Icon(
                            Icons.Default.LocationOn,
                            null,
                            tint = SakuraPink
                        )
                    },

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(14.dp),

                    colors = nihonFieldColors(),

                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── Ngày sinh ───────────────────────────

                SectionLabel("Ngày sinh")

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    // Day
                    DropdownSelector(
                        modifier = Modifier.weight(1f),

                        label = "Ngày",

                        selectedValue =
                            selectedDay.toString(),

                        items = days.map { it.toString() }

                    ) {
                        selectedDay = it.toInt()
                    }

                    // Month
                    DropdownSelector(
                        modifier = Modifier.weight(1f),

                        label = "Tháng",

                        selectedValue =
                            selectedMonth.toString(),

                        items = months.map { it.toString() }

                    ) {
                        selectedMonth = it.toInt()
                    }

                    // Year
                    DropdownSelector(
                        modifier = Modifier.weight(1f),

                        label = "Năm",

                        selectedValue =
                            selectedYear.toString(),

                        items = years.map { it.toString() }

                    ) {
                        selectedYear = it.toInt()
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Giới tính ───────────────────────────

                SectionLabel("Giới tính")

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    GENDER_LIST.forEach { item ->

                        SelectChip(
                            label = item,

                            isSelected = gender == item,

                            onClick = {
                                gender = item
                            },

                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Trình độ ────────────────────────────

                SectionLabel("Trình độ tiếng Nhật")

                Spacer(modifier = Modifier.height(8.dp))

                LEVEL_LIST.chunked(3).forEach { row ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        row.forEach { level ->

                            SelectChip(
                                label = level,

                                isSelected =
                                    studyLevel == level,

                                onClick = {
                                    studyLevel = level
                                },

                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── Error ───────────────────────────────

                val error = localError ?: uiState.errorMessage

                if (error != null) {

                    Text(
                        text = error,

                        color = ErrorRed,

                        fontSize = 13.sp,

                        fontFamily = NotoSansJP,

                        modifier = Modifier.padding(
                            bottom = 12.dp
                        )
                    )
                }

                // ── Button ──────────────────────────────

                Button(
                    onClick = {

                        if (displayName.isBlank()) {

                            localError =
                                "Vui lòng nhập họ và tên"

                            return@Button
                        }

                        if (phoneNumber.isBlank()) {

                            localError =
                                "Vui lòng nhập số điện thoại"

                            return@Button
                        }

                        localError = null

                        viewModel.updateUserProfile(
                            displayName = displayName,

                            phoneNumber = phoneNumber,

                            birthDate = birthDate,

                            gender = gender,

                            studyLevel = studyLevel,

                            address = address,

                            photoUrl = selectedAvatar,

                            onSuccess = {

                                if (isEditMode) {
                                    onNavigateBack()
                                } else {
                                    onNavigateToHome()
                                }
                            }
                        )
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),

                    shape = RoundedCornerShape(14.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = NihonRed,
                        contentColor = TextPrimary
                    ),

                    enabled = !uiState.isLoading
                ) {

                    if (uiState.isLoading) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),

                            color = TextPrimary,

                            strokeWidth = 2.dp
                        )

                    } else {

                        Text(
                            text = if (isEditMode)
                                "Lưu thay đổi"
                            else
                                "Hoàn thành",

                            fontFamily = NotoSansJP,

                            fontWeight = FontWeight.Bold,

                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────
// Dropdown Selector
// ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    modifier: Modifier = Modifier,
    label: String,
    selectedValue: String,
    items: List<String>,
    onSelected: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,

        onExpandedChange = {
            expanded = !expanded
        },

        modifier = modifier
    ) {

        OutlinedTextField(
            value = selectedValue,

            onValueChange = {},

            readOnly = true,

            label = {
                Text(label)
            },

            trailingIcon = {

                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },

            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),

            colors = nihonFieldColors(),

            shape = RoundedCornerShape(14.dp),

            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,

            onDismissRequest = {
                expanded = false
            }
        ) {

            items.forEach { item ->

                DropdownMenuItem(
                    text = {
                        Text(item)
                    },

                    onClick = {

                        onSelected(item)

                        expanded = false
                    }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────

@Composable
private fun nihonFieldColors() =
    OutlinedTextFieldDefaults.colors(

        focusedTextColor = TextPrimary,

        unfocusedTextColor = TextPrimary,

        focusedBorderColor = NihonRedLight,

        unfocusedBorderColor = CardBorder,

        focusedContainerColor = InkDark,

        unfocusedContainerColor = InkDark
    )

@Composable
private fun SectionLabel(text: String) {

    Text(
        text = text,

        fontFamily = NotoSansJP,

        fontWeight = FontWeight.Medium,

        color = TextSecondary,

        fontSize = 14.sp,

        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun AvatarItem(
    avatar: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(52.dp)

            .clip(CircleShape)

            .background(
                if (isSelected)
                    NihonRed.copy(alpha = 0.25f)
                else
                    InkDark
            )

            .border(
                width = if (isSelected) 2.dp else 1.dp,

                color = if (isSelected)
                    NihonRedLight
                else
                    CardBorder,

                shape = CircleShape
            )

            .clickable {
                onClick()
            },

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = avatar,
            fontSize = 28.sp
        )
    }
}

@Composable
fun SelectChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier

            .clip(RoundedCornerShape(10.dp))

            .background(
                if (isSelected)
                    NihonRed.copy(alpha = 0.2f)
                else
                    InkDark
            )

            .border(
                1.dp,

                if (isSelected)
                    NihonRedLight
                else
                    CardBorder,

                RoundedCornerShape(10.dp)
            )

            .clickable {
                onClick()
            }

            .padding(vertical = 10.dp),

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = label,

            color = if (isSelected)
                TextPrimary
            else
                TextSecondary,

            fontWeight = if (isSelected)
                FontWeight.Bold
            else
                FontWeight.Normal,

            fontSize = 13.sp
        )
    }
}