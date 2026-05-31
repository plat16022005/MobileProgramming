package com.group02.mobile.ui.screens.alphabet

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.data.model.alphabet.KanaType
import com.group02.mobile.data.repository.KanaRepository
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.KanaViewModel

package com.group02.mobile.ui.screens.alphabet

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.data.model.alphabet.KanaType
import com.group02.mobile.data.repository.KanaRepository
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.KanaViewModel
import com.group02.mobile.viewmodel.SharedPracticeViewModel

@Composable
fun KanaFlashCardRoute(
    rowId: String,
    kanaType: KanaType,
    viewModel: KanaViewModel,
    onNavigateBack: () -> Unit
) {
    val row = KanaRepository.getRowById(rowId) ?: return
    val currentIndex by viewModel.currentCardIndex.collectAsState()
    val isFlipped by viewModel.isCardFlipped.collectAsState()
    val char = row.characters.getOrNull(currentIndex) ?: return

    val frontText = KanaRepository.getCharacterDisplay(char, kanaType)
    val backText = char.romaji
    
    val exampleWord = if (kanaType == KanaType.KATAKANA && char.exampleWordKatakana.isNotEmpty()) char.exampleWordKatakana else char.exampleWord
    val exampleMeaning = if (kanaType == KanaType.KATAKANA && char.exampleWordKatakana.isNotEmpty()) char.exampleWordKatakanaMeaning else char.exampleWordMeaning

    FlashCardContent(
        title = "Flash Card — ${row.rowNameDisplay}",
        frontText = frontText,
        backText = backText,
        exampleWord = exampleWord,
        exampleMeaning = exampleMeaning,
        currentIndex = currentIndex,
        totalCards = row.characters.size,
        isFlipped = isFlipped,
        onFlip = { viewModel.flipCard() },
        onNext = { viewModel.nextCard(row.characters.size) },
        onPrev = { viewModel.prevCard() },
        onNavigateBack = {
            viewModel.resetCards()
            onNavigateBack()
        }
    )
}

@Composable
fun CustomFlashCardRoute(
    viewModel: SharedPracticeViewModel,
    onNavigateBack: () -> Unit
) {
    val currentIndex by viewModel.currentCardIndex.collectAsState()
    val isFlipped by viewModel.isCardFlipped.collectAsState()
    val vocabularies by viewModel.vocabularies.collectAsState()
    
    if (vocabularies.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(InkBlack), contentAlignment = Alignment.Center) {
            Text("Không có từ vựng nào.", color = TextPrimary)
        }
        return
    }

    val vocab = vocabularies.getOrNull(currentIndex) ?: return
    
    val frontText = if (vocab.kanji.isNotEmpty()) vocab.kanji else vocab.hiragana
    val backText = vocab.meaning
    val exampleWord = if (vocab.kanji.isNotEmpty()) vocab.hiragana else ""
    val exampleMeaning = vocab.romaji

    FlashCardContent(
        title = "Flash Card Cá Nhân",
        frontText = frontText,
        backText = backText,
        exampleWord = exampleWord,
        exampleMeaning = exampleMeaning,
        currentIndex = currentIndex,
        totalCards = vocabularies.size,
        isFlipped = isFlipped,
        onFlip = { viewModel.flipCard() },
        onNext = { viewModel.nextCard() },
        onPrev = { viewModel.prevCard() },
        onNavigateBack = {
            viewModel.resetCards()
            onNavigateBack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashCardContent(
    title: String,
    frontText: String,
    backText: String,
    exampleWord: String,
    exampleMeaning: String,
    currentIndex: Int,
    totalCards: Int,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
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
                // Card with 3D Flip Animation
                val rotationY by animateFloatAsState(
                    targetValue = if (isFlipped) 180f else 0f,
                    animationSpec = tween(durationMillis = 400),
                    label = "flipAnimation"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .aspectRatio(3f / 4f)
                        .clickable { onFlip() }
                        .graphicsLayer {
                            this.rotationY = rotationY
                            cameraDistance = 12f * density
                        }
                        .border(2.dp, CardBorder, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = InkDark)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rotationY <= 90f) {
                            // Front
                            Text(
                                text = frontText,
                                fontSize = if (frontText.length > 2) 72.sp else 120.sp,
                                color = SakuraPink,
                                fontFamily = NotoSansJP,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            // Back
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.graphicsLayer {
                                    this.rotationY = 180f
                                }.padding(16.dp)
                            ) {
                                Text(
                                    text = backText,
                                    fontSize = if (backText.length > 10) 36.sp else 48.sp,
                                    color = TextPrimary,
                                    fontFamily = NotoSansJP,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )

                                if (exampleWord.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = exampleWord,
                                        fontSize = 24.sp,
                                        color = TextSecondary,
                                        fontFamily = NotoSansJP,
                                        textAlign = TextAlign.Center
                                    )
                                    if (exampleMeaning.isNotEmpty()) {
                                        Text(
                                            text = exampleMeaning,
                                            fontSize = 16.sp,
                                            color = NihonRedLight,
                                            fontFamily = NotoSansJP,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Tap để lật thẻ",
                    fontSize = 14.sp,
                    color = TextHint,
                    fontFamily = NotoSansJP
                )

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
                        text = "${currentIndex + 1} / $totalCards",
                        color = TextSecondary,
                        fontFamily = NotoSansJP
                    )
                    
                    Button(
                        onClick = onNext,
                        enabled = currentIndex < totalCards - 1,
                        colors = ButtonDefaults.buttonColors(containerColor = NihonRedLight)
                    ) {
                        Text("Tiếp ▶", color = TextPrimary)
                    }
                }
            }
        }
    }
}
