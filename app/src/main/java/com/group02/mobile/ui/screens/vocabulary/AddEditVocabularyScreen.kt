package com.group02.mobile.ui.screens.vocabulary

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.data.model.vocabulary.UserVocabulary
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.UiState
import com.group02.mobile.viewmodel.UserVocabularyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVocabularyScreen(
    vocabularyId: String?,
    viewModel: UserVocabularyViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val vocabularyList by viewModel.vocabularyList.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val editingItem = remember(vocabularyId, vocabularyList) {
        vocabularyList.find { it.id == vocabularyId }
    }

    var kanji by remember { mutableStateOf(editingItem?.kanji ?: "") }
    var hiragana by remember { mutableStateOf(editingItem?.hiragana ?: "") }
    var romaji by remember { mutableStateOf(editingItem?.romaji ?: "") }
    var meaning by remember { mutableStateOf(editingItem?.meaning ?: "") }

    var importMessage by remember { mutableStateOf<String?>(null) }

    val csvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.importFromCsv(it, context) { count, errors ->
                importMessage = "Đã nhập thành công $count từ vựng."
            }
        }
    }

    val isFormValid = hiragana.isNotBlank() && meaning.isNotBlank()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (editingItem == null) "Thêm từ mới" else "Chỉnh sửa từ",
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
            OutlinedTextField(
                value = kanji,
                onValueChange = { kanji = it },
                label = { Text("Kanji / Katakana (không bắt buộc)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SakuraPink,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            OutlinedTextField(
                value = hiragana,
                onValueChange = { hiragana = it },
                label = { Text("Hiragana *") },
                isError = hiragana.isBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SakuraPink,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    errorBorderColor = NihonRedLight
                )
            )

            OutlinedTextField(
                value = romaji,
                onValueChange = { romaji = it },
                label = { Text("Romaji") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SakuraPink,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            OutlinedTextField(
                value = meaning,
                onValueChange = { meaning = it },
                label = { Text("Ý nghĩa (Tiếng Việt) *") },
                isError = meaning.isBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SakuraPink,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    errorBorderColor = NihonRedLight
                )
            )

            Button(
                onClick = {
                    if (isFormValid) {
                        if (editingItem == null) {
                            viewModel.addVocabulary(kanji, hiragana, romaji, meaning)
                        } else {
                            viewModel.updateVocabulary(
                                editingItem.copy(
                                    kanji = kanji,
                                    hiragana = hiragana,
                                    romaji = romaji,
                                    meaning = meaning
                                )
                            )
                        }
                        onNavigateBack()
                    }
                },
                enabled = isFormValid && uiState !is UiState.Loading,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NihonRedLight)
            ) {
                Text(if (editingItem == null) "Lưu từ mới" else "Cập nhật", color = TextPrimary)
            }

            if (editingItem == null) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = CardBorder)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Hoặc import từ file CSV", color = TextSecondary)
                Button(
                    onClick = { csvLauncher.launch("*/*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = InkDark)
                ) {
                    Text("Chọn file CSV", color = TextPrimary)
                }

                if (importMessage != null) {
                    Text(importMessage!!, color = SakuraPink)
                }
                if (uiState is UiState.Error) {
                    Text((uiState as UiState.Error).message, color = NihonRedLight)
                }
            }
        }
    }
}
