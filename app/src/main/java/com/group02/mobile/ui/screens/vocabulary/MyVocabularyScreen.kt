package com.group02.mobile.ui.screens.vocabulary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.data.model.vocabulary.UserVocabulary
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.UiState
import com.group02.mobile.viewmodel.UserVocabularyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyVocabularyScreen(
    viewModel: UserVocabularyViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddEdit: (String?) -> Unit
) {
    val vocabularyList by viewModel.filteredList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showDeleteConfirmDialog by remember { mutableStateOf<UserVocabulary?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Từ của tôi",
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddEdit(null) },
                containerColor = NihonRedLight,
                contentColor = TextPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm từ mới")
            }
        },
        containerColor = InkBlack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Tìm kiếm từ hoặc nghĩa...", color = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SakuraPink,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = SakuraPink
                ),
                singleLine = true
            )

            when {
                uiState is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SakuraPink)
                    }
                }
                vocabularyList.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Không tìm thấy từ nào." else "Chưa có từ vựng nào.\nHãy thêm từ đầu tiên!",
                            color = TextSecondary,
                            fontFamily = NotoSansJP,
                            fontSize = 16.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(vocabularyList, key = { it.id }) { item ->
                            VocabularyItem(
                                item = item,
                                onEdit = { onNavigateToAddEdit(item.id) },
                                onDelete = { showDeleteConfirmDialog = item }
                            )
                        }
                    }
                }
            }
        }

        // Delete Dialog
        if (showDeleteConfirmDialog != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = null },
                title = { Text("Xoá từ vựng") },
                text = { Text("Bạn có chắc chắn muốn xoá từ '${showDeleteConfirmDialog?.hiragana}' không?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteConfirmDialog?.let { viewModel.deleteVocabulary(it.id) }
                        showDeleteConfirmDialog = null
                    }) {
                        Text("Xoá", color = NihonRedLight)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = null }) {
                        Text("Hủy", color = TextPrimary)
                    }
                },
                containerColor = InkDark,
                titleContentColor = TextPrimary,
                textContentColor = TextSecondary
            )
        }
    }
}

@Composable
fun VocabularyItem(
    item: UserVocabulary,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = InkDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val displayWord = if (item.kanji.isNotEmpty()) "${item.kanji} (${item.hiragana})" else item.hiragana
                Text(
                    text = displayWord,
                    fontFamily = NotoSansJP,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = SakuraPink
                )
                if (item.romaji.isNotEmpty()) {
                    Text(
                        text = item.romaji,
                        fontFamily = NotoSansJP,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.meaning,
                    fontFamily = NotoSansJP,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = TextSecondary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Xoá", tint = NihonRedLight)
            }
        }
    }
}
