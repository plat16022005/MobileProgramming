package com.group02.mobile.ui.screens.alphabet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.data.model.alphabet.KanaRow
import com.group02.mobile.data.model.alphabet.KanaType
import com.group02.mobile.data.repository.KanaRepository
import com.group02.mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanaRowDetailScreen(
    rowId: String,
    kanaType: KanaType,
    onNavigateBack: () -> Unit,
    onNavigateToStudy: () -> Unit,
    onNavigateToWriting: () -> Unit,
    onNavigateToFlashCard: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToChallenge: () -> Unit
) {
    val row = KanaRepository.getRowById(rowId) ?: return

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
                        text = row.rowNameDisplay,
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Preview Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(row.characters) { char ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .background(InkDark, RoundedCornerShape(8.dp))
                                .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = KanaRepository.getCharacterDisplay(char, kanaType),
                                fontSize = 24.sp,
                                color = SakuraPink,
                                fontFamily = NotoSansJP,
                                fontWeight = FontWeight.Bold
                            )
                            if (char.romaji.isNotEmpty()) {
                                Text(
                                    text = char.romaji,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontFamily = NotoSansJP
                                )
                            }
                        }
                    }
                }

                // 5 Mode Buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ModeButton("Học Từ", Icons.Default.MenuBook, onNavigateToStudy)
                    ModeButton("Luyện Viết", Icons.Default.Edit, onNavigateToWriting)
                    ModeButton("Flash Card", Icons.Default.Style, onNavigateToFlashCard)
                    ModeButton("Trắc Nghiệm", Icons.Default.Quiz, onNavigateToQuiz)
                    ModeButton("Thử Thách", Icons.Default.Timer, onNavigateToChallenge)
                }
            }
        }
    }
}

@Composable
fun ModeButton(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = InkDark),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SakuraPink,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontFamily = NotoSansJP
            )
        }
    }
}
