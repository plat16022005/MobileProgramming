package com.group02.mobile.ui.screens.alphabet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group02.mobile.data.model.alphabet.KanaRow
import com.group02.mobile.data.model.alphabet.KanaType
import com.group02.mobile.data.repository.KanaRepository
import com.group02.mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanaRowListScreen(
    kanaType: KanaType,
    onNavigateBack: () -> Unit,
    onRowSelected: (KanaRow) -> Unit
) {
    val basicRows = KanaRepository.getBasicRows()
    val dakutenRows = KanaRepository.getDakutenRows()
    val youonRows = KanaRepository.getYouonRows()

    val title = if (kanaType == KanaType.HIRAGANA) "Hiragana" else "Katakana"

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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item { SectionHeader("Bảng Cơ Bản (Gojuuon)") }
                items(basicRows) { row ->
                    RowItem(row, kanaType, onRowSelected)
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { SectionHeader("Âm Đục (Dakuten)") }
                items(dakutenRows) { row ->
                    RowItem(row, kanaType, onRowSelected)
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { SectionHeader("Âm Ghép (Youon)") }
                items(youonRows) { row ->
                    RowItem(row, kanaType, onRowSelected)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = SakuraPink,
        modifier = Modifier.padding(vertical = 8.dp),
        fontFamily = NotoSansJP
    )
}

@Composable
fun RowItem(
    row: KanaRow,
    kanaType: KanaType,
    onClick: (KanaRow) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(row) }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = row.rowNameDisplay,
            fontSize = 16.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            fontFamily = NotoSansJP,
            modifier = Modifier.weight(1f)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            row.characters.take(5).forEach { char ->
                val displayChar = KanaRepository.getCharacterDisplay(char, kanaType)
                Text(
                    text = displayChar,
                    fontSize = 18.sp,
                    color = TextSecondary,
                    fontFamily = NotoSansJP
                )
            }
        }
    }
    HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
}
