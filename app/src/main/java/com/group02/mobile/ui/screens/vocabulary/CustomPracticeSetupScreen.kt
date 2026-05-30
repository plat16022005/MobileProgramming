package com.group02.mobile.ui.screens.vocabulary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.data.model.vocabulary.PracticeMode
import com.group02.mobile.data.model.vocabulary.UserVocabulary
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.CustomPracticeViewModel
import com.group02.mobile.viewmodel.UserVocabularyViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPracticeSetupScreen(
    customViewModel: CustomPracticeViewModel,
    userVocabViewModel: UserVocabularyViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPractice: (PracticeMode) -> Unit
) {
    val selectedVocabs by customViewModel.selectedVocabularies.collectAsState()
    val practiceMode by customViewModel.practiceMode.collectAsState()
    val allVocabs by userVocabViewModel.vocabularyList.collectAsState()
    val startPractice by customViewModel.startPractice.collectAsState()

    var showManualAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(startPractice) {
        if (startPractice && practiceMode != null) {
            onNavigateToPractice(practiceMode!!)
            customViewModel.onPracticeStarted()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Luyện tập Tùy chỉnh",
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
        },
        containerColor = InkBlack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("1. Chọn nguồn từ vựng", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { customViewModel.setSelectedVocabularies(allVocabs) },
                    colors = ButtonDefaults.buttonColors(containerColor = InkDark),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Dùng toàn bộ kho", color = SakuraPink)
                }
                Button(
                    onClick = { showManualAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = InkDark),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Nhập nhanh", color = SakuraPink)
                }
            }

            Text("2. Từ đã chọn (${selectedVocabs.size})", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (selectedVocabs.isEmpty()) {
                Text("Chưa chọn từ vựng nào.", color = TextSecondary)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(selectedVocabs) { item ->
                        InputChip(
                            selected = false,
                            onClick = { },
                            label = { Text(item.hiragana, color = TextPrimary) },
                            trailingIcon = {
                                IconButton(
                                    onClick = { customViewModel.removeVocabulary(item.id) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Xóa", tint = SakuraPink)
                                }
                            },
                            colors = InputChipDefaults.inputChipColors(containerColor = InkDark)
                        )
                    }
                }
                TextButton(onClick = { customViewModel.clearSelection() }) {
                    Text("Xóa tất cả", color = NihonRedLight)
                }
            }

            Text("3. Chọn chế độ luyện tập", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            
            val modes = listOf(
                PracticeMode.STUDY to "Học từ",
                PracticeMode.FLASHCARD to "Flashcard",
                PracticeMode.QUIZ to "Trắc nghiệm",
                PracticeMode.CHALLENGE to "Thử thách"
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ModeCard(modes[0].first, modes[0].second, practiceMode == modes[0].first) { customViewModel.setPracticeMode(it) }
                    ModeCard(modes[1].first, modes[1].second, practiceMode == modes[1].first) { customViewModel.setPracticeMode(it) }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ModeCard(modes[2].first, modes[2].second, practiceMode == modes[2].first) { customViewModel.setPracticeMode(it) }
                    ModeCard(modes[3].first, modes[3].second, practiceMode == modes[3].first) { customViewModel.setPracticeMode(it) }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { customViewModel.startSession() },
                enabled = selectedVocabs.isNotEmpty() && practiceMode != null,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NihonRedLight)
            ) {
                Text("Bắt đầu Luyện tập", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (showManualAddDialog) {
            var hiragana by remember { mutableStateOf("") }
            var meaning by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showManualAddDialog = false },
                title = { Text("Nhập từ vựng nhanh") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = hiragana,
                            onValueChange = { hiragana = it },
                            label = { Text("Hiragana / Romaji") }
                        )
                        OutlinedTextField(
                            value = meaning,
                            onValueChange = { meaning = it },
                            label = { Text("Nghĩa") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (hiragana.isNotBlank() && meaning.isNotBlank()) {
                                customViewModel.addManualVocabulary(
                                    UserVocabulary(
                                        id = UUID.randomUUID().toString(),
                                        kanji = "",
                                        hiragana = hiragana,
                                        romaji = "",
                                        meaning = meaning
                                    )
                                )
                                hiragana = ""
                                meaning = ""
                                showManualAddDialog = false
                            }
                        }
                    ) {
                        Text("Thêm", color = SakuraPink)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showManualAddDialog = false }) {
                        Text("Đóng", color = TextPrimary)
                    }
                },
                containerColor = InkDark,
                titleContentColor = TextPrimary,
                textContentColor = TextPrimary
            )
        }
    }
}

@Composable
fun RowScope.ModeCard(mode: PracticeMode, title: String, isSelected: Boolean, onClick: (PracticeMode) -> Unit) {
    Card(
        modifier = Modifier
            .weight(1f)
            .clickable { onClick(mode) }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) SakuraPink else androidx.compose.ui.graphics.Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = InkDark)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(title, color = if (isSelected) SakuraPink else TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}
