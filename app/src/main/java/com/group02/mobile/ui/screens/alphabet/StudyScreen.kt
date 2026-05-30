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
import com.group02.mobile.viewmodel.CustomPracticeViewModel
import com.group02.mobile.utils.TtsManager
import com.group02.mobile.data.model.alphabet.KanaCharacter
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    rowId: String,
    kanaType: KanaType,
    viewModel: KanaViewModel,
    customPracticeViewModel: CustomPracticeViewModel? = null,
    onNavigateBack: () -> Unit
) {
    val characters = if (rowId == "custom") {
        customPracticeViewModel?.currentSession?.value?.selectedVocabularies?.map {
            KanaCharacter(
                hiragana = it.hiragana,
                katakana = it.hiragana,
                romaji = it.romaji,
                exampleWord = it.kanji.ifEmpty { it.hiragana },
                exampleWordRomaji = it.romaji,
                exampleWordMeaning = it.meaning,
                exampleWordKatakana = it.kanji.ifEmpty { it.hiragana },
                exampleWordKatakanaRomaji = it.romaji,
                exampleWordKatakanaMeaning = it.meaning
            )
        } ?: emptyList()
    } else {
        KanaRepository.getRowById(rowId)?.characters ?: emptyList()
    }
    val rowDisplay = if (rowId == "custom") "Tùy chỉnh" else KanaRepository.getRowById(rowId)?.rowNameDisplay ?: ""

    if (characters.isEmpty()) return
    var currentIndex by remember { mutableStateOf(0) }
    val char = characters.getOrNull(currentIndex) ?: return

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
                        text = "Học Từ — $rowDisplay",
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
                    text = KanaRepository.getCharacterDisplay(char, kanaType),
                    fontSize = 120.sp,
                    color = SakuraPink,
                    fontFamily = NotoSansJP,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = char.romaji,
                    fontSize = 32.sp,
                    color = TextPrimary,
                    fontFamily = NotoSansJP
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = CardBorder)
                Spacer(modifier = Modifier.height(32.dp))
                
                if (char.exampleWord.isNotEmpty()) {
                    Text(
                        text = "Ví dụ:",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        fontFamily = NotoSansJP
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = char.exampleWord,
                        fontSize = 32.sp,
                        color = TextPrimary,
                        fontFamily = NotoSansJP,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = char.exampleWordRomaji,
                        fontSize = 16.sp,
                        color = TextSecondary,
                        fontFamily = NotoSansJP
                    )
                    Text(
                        text = char.exampleWordMeaning,
                        fontSize = 18.sp,
                        color = NihonRedLight,
                        fontFamily = NotoSansJP
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { if (currentIndex > 0) currentIndex-- },
                        enabled = currentIndex > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = InkDark)
                    ) {
                        Text("◀ Trước", color = TextPrimary)
                    }
                    
                    Text(
                        text = "${currentIndex + 1} / ${characters.size}",
                        color = TextSecondary,
                        fontFamily = NotoSansJP
                    )
                    
                    Button(
                        onClick = { if (currentIndex < characters.size - 1) currentIndex++ },
                        enabled = currentIndex < characters.size - 1,
                        colors = ButtonDefaults.buttonColors(containerColor = NihonRedLight)
                    ) {
                        Text("Tiếp ▶", color = TextPrimary)
                    }
                }
            }
        }
    }
}
