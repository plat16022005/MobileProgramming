package com.group02.mobile.ui.screens.alphabet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.digitalink.Ink
import kotlinx.coroutines.launch
import com.group02.mobile.data.model.alphabet.KanaType
import com.group02.mobile.data.repository.KanaRepository
import com.group02.mobile.ui.theme.*
import com.group02.mobile.viewmodel.KanaViewModel
import com.group02.mobile.utils.DigitalInkManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingPracticeScreen(
    rowId: String,
    kanaType: KanaType,
    viewModel: KanaViewModel,
    onNavigateBack: () -> Unit
) {
    val row = KanaRepository.getRowById(rowId) ?: return
    val currentIndex by viewModel.currentWritingCharIndex.collectAsState()
    val char = row.characters.getOrNull(currentIndex) ?: return
    
    val paths = remember { mutableStateListOf<Path>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    
    // ML Kit Ink Capture
    val inkBuilder = remember { mutableStateOf<Ink.Builder>(Ink.builder()) }
    var strokeBuilder by remember { mutableStateOf<Ink.Stroke.Builder>(Ink.Stroke.builder()) }
    
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isCorrectFeedback by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        DigitalInkManager.setup("ja")
    }

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
                        text = "Luyện Viết — ${char.romaji}",
                        fontFamily = NotoSansJP,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetWritingPractice()
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        paths.clear()
                        currentPath = null
                        inkBuilder.value = Ink.builder()
                        strokeBuilder = Ink.Stroke.builder()
                        feedbackMessage = null
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xóa",
                            tint = NihonRedLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = InkDark
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Reference Character
                Text(
                    text = KanaRepository.getCharacterDisplay(char, kanaType),
                    fontSize = 64.sp,
                    color = SakuraPink.copy(alpha = 0.5f),
                    fontFamily = NotoSansJP,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(InkDark, RoundedCornerShape(12.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPath = Path().apply {
                                        moveTo(offset.x, offset.y)
                                    }
                                    strokeBuilder = Ink.Stroke.builder()
                                    strokeBuilder.addPoint(Ink.Point.create(offset.x, offset.y))
                                },
                                onDrag = { change, _ ->
                                    val offset = change.position
                                    currentPath?.lineTo(offset.x, offset.y)
                                    strokeBuilder.addPoint(Ink.Point.create(offset.x, offset.y))
                                    // Trigger recomposition
                                    val tempPath = currentPath
                                    currentPath = null
                                    currentPath = tempPath
                                },
                                onDragEnd = {
                                    currentPath?.let { paths.add(it) }
                                    currentPath = null
                                    inkBuilder.value.addStroke(strokeBuilder.build())
                                }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Draw grid lines
                        val gridSize = size.width / 3
                        for (i in 1..2) {
                            drawLine(
                                color = CardBorder.copy(alpha = 0.5f),
                                start = Offset(x = gridSize * i, y = 0f),
                                end = Offset(x = gridSize * i, y = size.height),
                                strokeWidth = 1f
                            )
                            drawLine(
                                color = CardBorder.copy(alpha = 0.5f),
                                start = Offset(x = 0f, y = gridSize * i),
                                end = Offset(x = size.width, y = gridSize * i),
                                strokeWidth = 1f
                            )
                        }

                        // Draw paths
                        val drawPathParams = Stroke(
                            width = 12f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                        paths.forEach { path ->
                            drawPath(
                                path = path,
                                color = SakuraPink,
                                style = drawPathParams
                            )
                        }
                        currentPath?.let { path ->
                            drawPath(
                                path = path,
                                color = SakuraPink,
                                style = drawPathParams
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Feedback Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    feedbackMessage?.let { msg ->
                        Text(
                            text = msg,
                            color = if (isCorrectFeedback) SuccessGreen else NihonRedLight,
                            fontSize = 16.sp,
                            fontFamily = NotoSansJP,
                            fontWeight = FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val ink = inkBuilder.value.build()
                        if (ink.strokes.isNotEmpty()) {
                            DigitalInkManager.recognize(ink) { recognizedText, _ ->
                                val target = KanaRepository.getCharacterDisplay(char, kanaType)
                                isCorrectFeedback = recognizedText == target
                                
                                feedbackMessage = if (isCorrectFeedback) {
                                    "Chính xác! Bạn viết rất tốt."
                                } else if (recognizedText.isNotEmpty()) {
                                    "Bạn vừa viết chữ '$recognizedText'. Hãy thử lại nhé."
                                } else {
                                    "Chưa nhận diện được. Hãy viết rõ ràng hơn."
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SakuraPink)
                ) {
                    Text("Kiểm tra kết quả", color = InkBlack, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            viewModel.prevWritingChar()
                            paths.clear()
                            currentPath = null
                            inkBuilder.value = Ink.builder()
                            feedbackMessage = null
                        },
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
                        onClick = {
                            viewModel.nextWritingChar(row.characters.size)
                            paths.clear()
                            currentPath = null
                            inkBuilder.value = Ink.builder()
                            feedbackMessage = null
                        },
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
