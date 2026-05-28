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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashCardScreen(
    rowId: String,
    kanaType: KanaType,
    viewModel: KanaViewModel,
    onNavigateBack: () -> Unit
) {
    val row = KanaRepository.getRowById(rowId) ?: return
    val currentIndex by viewModel.currentCardIndex.collectAsState()
    val isFlipped by viewModel.isCardFlipped.collectAsState()
    val char = row.characters.getOrNull(currentIndex) ?: return

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
                        text = "Flash Card — ${row.rowNameDisplay}",
                        fontFamily = NotoSansJP,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetCards()
                        onNavigateBack()
                    }) {
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
                        .clickable { viewModel.flipCard() }
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
                                text = KanaRepository.getCharacterDisplay(char, kanaType),
                                fontSize = 120.sp,
                                color = SakuraPink,
                                fontFamily = NotoSansJP,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            // Back
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.graphicsLayer {
                                    this.rotationY = 180f
                                }
                            ) {
                                Text(
                                    text = char.romaji,
                                    fontSize = 48.sp,
                                    color = TextPrimary,
                                    fontFamily = NotoSansJP,
                                    fontWeight = FontWeight.Bold
                                )
                                if (char.exampleWord.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = char.exampleWord,
                                        fontSize = 24.sp,
                                        color = TextSecondary,
                                        fontFamily = NotoSansJP
                                    )
                                    Text(
                                        text = char.exampleWordMeaning,
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
                        onClick = { viewModel.prevCard() },
                        enabled = currentIndex > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = InkDark)
                    ) {
                        Text("◀ Trước", color = TextPrimary)
                    }
                    
                    Text(
                        text = "${currentIndex + 1} / ${row.characters.size}",
                        color = TextSecondary,
                        fontFamily = NotoSansJP
                    )
                    
                    Button(
                        onClick = { viewModel.nextCard(row.characters.size) },
                        enabled = currentIndex < row.characters.size - 1,
                        colors = ButtonDefaults.buttonColors(containerColor = NihonRedLight)
                    ) {
                        Text("Tiếp ▶", color = TextPrimary)
                    }
                }
            }
        }
    }
}
