package com.group02.mobile.ui.screens.alphabet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.data.model.alphabet.KanaType
import com.group02.mobile.data.repository.KanaRepository
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.KanaViewModel
import com.group02.mobile.viewmodel.SharedPracticeViewModel
import com.group02.mobile.utils.TtsManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun KanaStudyRoute(
    rowId: String,
    kanaType: KanaType,
    viewModel: KanaViewModel,
    onNavigateBack: () -> Unit
) {
    val row = KanaRepository.getRowById(rowId) ?: return
    var currentIndex by remember { mutableStateOf(0) }
    val char = row.characters.getOrNull(currentIndex) ?: return

    val context = LocalContext.current

    val mainText = KanaRepository.getCharacterDisplay(char, kanaType)
    val subText = char.romaji
    
    val exampleWord = if (kanaType == KanaType.KATAKANA && char.exampleWordKatakana.isNotEmpty()) char.exampleWordKatakana else char.exampleWord
    val exampleRomaji = if (kanaType == KanaType.KATAKANA && char.exampleWordKatakana.isNotEmpty()) char.exampleWordKatakanaRomaji else char.exampleWordRomaji
    val exampleMeaning = if (kanaType == KanaType.KATAKANA && char.exampleWordKatakana.isNotEmpty()) char.exampleWordKatakanaMeaning else char.exampleWordMeaning

    StudyContent(
        title = "Học Từ — ${row.rowNameDisplay}",
        mainText = mainText,
        subText = subText,
        exampleWord = exampleWord,
        exampleRomaji = exampleRomaji,
        exampleMeaning = exampleMeaning,
        currentIndex = currentIndex,
        totalItems = row.characters.size,
        onPrev = { if (currentIndex > 0) currentIndex-- },
        onNext = { if (currentIndex < row.characters.size - 1) currentIndex++ },
        onPlayMainAudio = { TtsManager.speak(context, mainText, subText) },
        onPlayExampleAudio = { TtsManager.speak(context, exampleWord, exampleRomaji) },
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun CustomStudyRoute(
    viewModel: SharedPracticeViewModel,
    onNavigateBack: () -> Unit
) {
    val vocabularies by viewModel.vocabularies.collectAsState()
    var currentIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current

    if (vocabularies.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(InkBlack), contentAlignment = Alignment.Center) {
            Text("Không có từ vựng nào.", color = TextPrimary)
        }
        return
    }

    val vocab = vocabularies.getOrNull(currentIndex) ?: return
    
    val mainText = if (vocab.kanji.isNotEmpty()) vocab.kanji else vocab.hiragana
    val subText = if (vocab.kanji.isNotEmpty()) vocab.hiragana else vocab.romaji
    
    val exampleWord = ""
    val exampleRomaji = vocab.romaji
    val exampleMeaning = vocab.meaning

    StudyContent(
        title = "Học Từ Cá Nhân",
        mainText = mainText,
        subText = subText,
        exampleWord = exampleWord,
        exampleRomaji = exampleRomaji,
        exampleMeaning = exampleMeaning,
        currentIndex = currentIndex,
        totalItems = vocabularies.size,
        onPrev = { if (currentIndex > 0) currentIndex-- },
        onNext = { if (currentIndex < vocabularies.size - 1) currentIndex++ },
        onPlayMainAudio = { TtsManager.speak(context, mainText, vocab.romaji) },
        onPlayExampleAudio = { }, // No example for custom vocabs
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyContent(
    title: String,
    mainText: String,
    subText: String,
    exampleWord: String,
    exampleRomaji: String,
    exampleMeaning: String,
    currentIndex: Int,
    totalItems: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onPlayMainAudio: () -> Unit,
    onPlayExampleAudio: () -> Unit,
    onNavigateBack: () -> Unit
) {
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
                        text = title,
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = mainText,
                    fontSize = if (mainText.length > 2) 72.sp else 120.sp,
                    color = SakuraPink,
                    fontFamily = NotoSansJP,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = subText,
                    fontSize = 32.sp,
                    color = TextPrimary,
                    fontFamily = NotoSansJP,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                IconButton(
                    onClick = onPlayMainAudio,
                    modifier = Modifier
                        .background(InkDark, RoundedCornerShape(50))
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Phát âm",
                        tint = SakuraPink,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = CardBorder)
                Spacer(modifier = Modifier.height(32.dp))

                if (exampleWord.isNotEmpty() || exampleMeaning.isNotEmpty()) {
                    Text(
                        text = if (exampleWord.isNotEmpty()) "Ví dụ:" else "Nghĩa:",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        fontFamily = NotoSansJP
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (exampleWord.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = exampleWord,
                                fontSize = 32.sp,
                                color = TextPrimary,
                                fontFamily = NotoSansJP,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = onPlayExampleAudio) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Phát âm",
                                    tint = SakuraPink
                                )
                            }
                        }
                    }
                    if (exampleRomaji.isNotEmpty()) {
                        Text(
                            text = exampleRomaji,
                            fontSize = 16.sp,
                            color = TextSecondary,
                            fontFamily = NotoSansJP,
                            textAlign = TextAlign.Center
                        )
                    }
                    if (exampleMeaning.isNotEmpty()) {
                        Text(
                            text = exampleMeaning,
                            fontSize = 18.sp,
                            color = NihonRedLight,
                            fontFamily = NotoSansJP,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onPrev,
                        enabled = currentIndex > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = InkDark)
                    ) {
                        Text("◀ Trước", color = TextPrimary)
                    }
                    
                    Text(
                        text = "${currentIndex + 1} / $totalItems",
                        color = TextSecondary,
                        fontFamily = NotoSansJP
                    )
                    
                    Button(
                        onClick = onNext,
                        enabled = currentIndex < totalItems - 1,
                        colors = ButtonDefaults.buttonColors(containerColor = NihonRedLight)
                    ) {
                        Text("Tiếp ▶", color = TextPrimary)
                    }
                }
            }
        }
    }
}
