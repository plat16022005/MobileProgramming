package com.group02.mobile.ui.screens.alphabet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.DictionaryViewModel
import com.group02.mobile.utils.TtsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(
    viewModel: DictionaryViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMyVocabulary: () -> Unit = {}
) {
    val words by viewModel.words.collectAsState()
    val currentLevel by viewModel.currentLevel.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isEndReached by viewModel.isEndReached.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var expanded by remember { mutableStateOf(false) }

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
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Từ Điển ($currentLevel)",
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
                actions = {
                    IconButton(onClick = onNavigateToMyVocabulary) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "Từ của tôi", tint = SakuraPink)
                    }
                    Box {
                        TextButton(onClick = { expanded = true }) {
                            Text(text = currentLevel, color = SakuraPink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = SakuraPink)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(InkDark)
                        ) {
                            viewModel.levels.forEach { level ->
                                DropdownMenuItem(
                                    text = { Text(level, color = TextPrimary) },
                                    onClick = {
                                        viewModel.setLevel(level)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = InkDark
                )
            )

            if (error != null && words.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Lỗi: $error", color = NihonRedLight)
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(words) { index, wordItem ->
                        // Request load more when near end
                        if (index == words.lastIndex && !isLoading && !isEndReached) {
                            LaunchedEffect(index) {
                                viewModel.loadMoreWords()
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = InkDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        val displayWord = if (wordItem.word.isNotEmpty()) wordItem.word else wordItem.hiragana
                                        Text(
                                            text = displayWord,
                                            fontSize = 28.sp,
                                            color = TextPrimary,
                                            fontFamily = NotoSansJP,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (wordItem.hiragana.isNotEmpty() && wordItem.hiragana != wordItem.word) {
                                            Text(
                                                text = wordItem.hiragana,
                                                fontSize = 16.sp,
                                                color = SakuraPink,
                                                fontFamily = NotoSansJP
                                            )
                                        }
                                        if (wordItem.romaji.isNotEmpty()) {
                                            Text(
                                                text = wordItem.romaji,
                                                fontSize = 14.sp,
                                                color = TextSecondary,
                                                fontFamily = NotoSansJP
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = {
                                            val speakText = if (wordItem.word.isNotEmpty()) wordItem.word else wordItem.hiragana
                                            TtsManager.speak(context, speakText, "")
                                        },
                                        modifier = Modifier
                                            .background(InkBlack, RoundedCornerShape(50))
                                            .padding(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Phát âm",
                                            tint = SakuraPink
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = CardBorder)
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Text(
                                    text = wordItem.meanings,
                                    fontSize = 16.sp,
                                    color = TextSecondary,
                                    fontFamily = NotoSansJP
                                )
                            }
                        }
                    }
                    
                    if (isLoading) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = SakuraPink)
                            }
                        }
                    }
                }
            }
        }
    }
}
