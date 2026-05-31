package com.group02.mobile.ui.screens.vocabulary

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group02.mobile.data.model.vocabulary.PracticeMode
import com.group02.mobile.viewmodel.CustomPracticeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPracticeSetupScreen(
    viewModel: CustomPracticeViewModel,
    onNavigateBack: () -> Unit,
    onStartPractice: (com.group02.mobile.data.model.vocabulary.PracticeSession) -> Unit
) {
    val selectedVocabularies by viewModel.selectedVocabularies.collectAsState()
    val selectedMode by viewModel.practiceMode.collectAsState()
    val isReady by viewModel.sessionReady.collectAsState()
    val importError by viewModel.importError.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.importFromCsv(uri)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.clearSelection()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Luyện tập Tùy chỉnh") },
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Phần 1: Chọn nguồn từ vựng
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Nguồn từ vựng", style = MaterialTheme.typography.titleMedium)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = { viewModel.loadAllMyVocabularies() }) {
                            Text("Từ kho của tôi")
                        }
                        Button(onClick = { filePickerLauncher.launch("text/*") }) {
                            Text("Import CSV")
                        }
                    }

                    if (importError != null) {
                        Text(text = importError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // Phần 2: Bộ từ đã chọn
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedVocabularies.size} từ đã chọn",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (selectedVocabularies.isNotEmpty()) {
                            TextButton(onClick = { viewModel.clearSelection() }) {
                                Text("Xóa hết")
                            }
                        }
                    }

                    if (selectedVocabularies.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(selectedVocabularies) { item ->
                                InputChip(
                                    selected = false,
                                    onClick = { },
                                    label = { Text(if (item.kanji.isNotEmpty()) item.kanji else item.hiragana) },
                                    trailingIcon = {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Xóa",
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable { viewModel.removeVocabulary(item.id) }
                                        )
                                    }
                                )
                            }
                        }
                    } else {
                        Text("Chưa có từ nào được chọn.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Phần 3: Chọn chế độ
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Chế độ luyện tập", style = MaterialTheme.typography.titleMedium)

                    val modes = listOf(
                        PracticeMode.STUDY to "Học từ",
                        PracticeMode.FLASHCARD to "Flashcard",
                        PracticeMode.QUIZ to "Trắc nghiệm",
                        PracticeMode.CHALLENGE to "Thử thách"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        modes.forEach { (mode, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setPracticeMode(mode) }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedMode == mode,
                                    onClick = { viewModel.setPracticeMode(mode) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(label, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val session = viewModel.startSession()
                    if (session != null) {
                        onStartPractice(session)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = isReady
            ) {
                Text("Bắt đầu luyện tập", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
