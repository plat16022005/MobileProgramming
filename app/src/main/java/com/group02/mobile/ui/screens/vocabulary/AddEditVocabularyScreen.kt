package com.group02.mobile.ui.screens.vocabulary

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group02.mobile.data.model.vocabulary.UserVocabulary
import com.group02.mobile.viewmodel.UiState
import com.group02.mobile.viewmodel.UserVocabularyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVocabularyScreen(
    vocabularyId: String?,
    viewModel: UserVocabularyViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val vocabularyList by viewModel.vocabularyList.collectAsState()
    
    val editingItem = remember(vocabularyId, vocabularyList) {
        vocabularyList.find { it.id == vocabularyId }
    }

    var kanji by remember { mutableStateOf(editingItem?.kanji ?: "") }
    var hiragana by remember { mutableStateOf(editingItem?.hiragana ?: "") }
    var romaji by remember { mutableStateOf(editingItem?.romaji ?: "") }
    var meaning by remember { mutableStateOf(editingItem?.meaning ?: "") }

    val isEditing = vocabularyId != null
    val isValid = hiragana.isNotBlank() && meaning.isNotBlank()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.importFromCsv(uri)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            viewModel.resetUiState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Chỉnh sửa từ" else "Thêm từ mới") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Trở về")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = kanji,
                onValueChange = { kanji = it },
                label = { Text("Kanji / Katakana (Tùy chọn)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = hiragana,
                onValueChange = { hiragana = it },
                label = { Text("Hiragana *") },
                modifier = Modifier.fillMaxWidth(),
                isError = hiragana.isBlank()
            )

            OutlinedTextField(
                value = romaji,
                onValueChange = { romaji = it },
                label = { Text("Romaji (Tùy chọn)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = meaning,
                onValueChange = { meaning = it },
                label = { Text("Ý nghĩa (Tiếng Việt) *") },
                modifier = Modifier.fillMaxWidth(),
                isError = meaning.isBlank()
            )

            Button(
                onClick = {
                    if (isEditing && editingItem != null) {
                        viewModel.updateVocabulary(
                            editingItem.copy(
                                kanji = kanji.trim(),
                                hiragana = hiragana.trim(),
                                romaji = romaji.trim(),
                                meaning = meaning.trim()
                            )
                        )
                    } else {
                        viewModel.addVocabulary(
                            kanji = kanji.trim(),
                            hiragana = hiragana.trim(),
                            romaji = romaji.trim(),
                            meaning = meaning.trim()
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isValid && uiState !is UiState.Loading
            ) {
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Lưu")
                }
            }

            if (!isEditing) {
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                Text(
                    text = "Hoặc import từ file CSV",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Button(
                    onClick = { filePickerLauncher.launch("text/*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Chọn file CSV")
                }
            }

            if (uiState is UiState.Error) {
                Text(
                    text = (uiState as UiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
