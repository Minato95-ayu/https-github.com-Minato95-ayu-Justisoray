package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EvidenceEntity
import com.example.ui.viewmodel.SimulationViewModel
import kotlin.math.roundToInt

@Composable
fun InvestigationScreen(
    viewModel: SimulationViewModel,
    onNavigateBack: () -> Unit
) {
    val selectedCase by viewModel.selectedCase.collectAsState()
    val evidenceList by viewModel.activeEvidence.collectAsState()

    var inspectorDetailItem by remember { mutableStateOf<EvidenceEntity?>(null) }

    // Center Anchor Hub coordinate coordinates
    val centerHubX = 400f
    val centerHubY = 600f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackgroundBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Core interactive pin-board sandbox canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Draw decorative vintage cork/cyber tactical board grid markings
                    val gap = 60f
                    for (x in 0..(this.size.width / gap).toInt()) {
                        drawCircle(
                            color = ColorTacticalCyan.copy(alpha = 0.08f),
                            radius = 2f,
                            center = Offset(x * gap, 0f)
                        )
                        for (y in 0..(this.size.height / gap).toInt()) {
                            drawCircle(
                                color = ColorTacticalCyan.copy(alpha = 0.08f),
                                radius = 2f,
                                center = Offset(x * gap, y * gap)
                            )
                        }
                    }
                }
        ) {
            // Draw connecting red string conduits from center hub database to evidence pins!
            Canvas(modifier = Modifier.fillMaxSize()) {
                evidenceList.forEach { ev ->
                    // Set color based on found/missing status
                    val conduitColor = if (ev.statusText == "FOUND") ColorCyberGreen else ColorBrightCoral
                    
                    // Draw thread line connection vectors
                    drawLine(
                        color = conduitColor.copy(alpha = 0.45f),
                        start = Offset(centerHubX, centerHubY),
                        end = Offset(ev.assignedNodeX + 160f, ev.assignedNodeY + 80f),
                        strokeWidth = 3.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                    
                    // Draw node target circles
                    drawCircle(
                        color = conduitColor.copy(alpha = 0.25f),
                        radius = 8f,
                        center = Offset(ev.assignedNodeX + 160f, ev.assignedNodeY + 80f)
                    )
                }
            }

            // Render Center Hub (Case File Node Anchor)
            Box(
                modifier = Modifier
                    .offset { IntOffset((centerHubX - 120f).roundToInt(), (centerHubY - 80f).roundToInt()) }
                    .width(240.dp)
                    .background(ColorSurfaceCard, RoundedCornerShape(14.dp))
                    .border(2.dp, ColorNeonAmber, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = "Case Anchor",
                        tint = ColorNeonAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = selectedCase?.title ?: "No Dossier Selected",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "INTELLIGENCE FOCUS POINT",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = ColorNeonAmber
                    )
                }
            }

            // Render surrounding Draggable Evidence Nodes
            evidenceList.forEach { ev ->
                var offsetX by remember(ev.id) { mutableStateOf(ev.assignedNodeX) }
                var offsetY by remember(ev.id) { mutableStateOf(ev.assignedNodeY) }

                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .width(160.dp)
                        .background(ColorSurfaceCard.copy(alpha = 0.95f), RoundedCornerShape(12.dp))
                        .border(
                            1.5.dp,
                            if (ev.statusText == "FOUND") ColorCyberGreen else ColorBrightCoral.copy(alpha = 0.7f),
                            RoundedCornerShape(12.dp)
                        )
                        .pointerInput(ev.id) {
                            detectDragGestures(
                                onDragEnd = {
                                    viewModel.updateEvidenceNodePosition(ev.id, offsetX, offsetY)
                                }
                            ) { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }
                        .clickable { inspectorDetailItem = ev }
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = when (ev.classification) {
                                    "Documentary" -> Icons.Default.Description
                                    "Digital" -> Icons.Default.Videocam
                                    "Witness Statement" -> Icons.Default.RecordVoiceOver
                                    else -> Icons.Default.Tag
                                },
                                contentDescription = ev.classification,
                                tint = if (ev.statusText == "FOUND") ColorCyberGreen else ColorBrightCoral,
                                modifier = Modifier.size(16.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .background(
                                        (if (ev.statusText == "FOUND") ColorCyberGreen else ColorBrightCoral).copy(alpha = 0.1f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = ev.statusText,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (ev.statusText == "FOUND") ColorCyberGreen else ColorBrightCoral
                                )
                            }
                        }

                        Text(
                            text = ev.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 2
                        )

                        Text(
                            text = ev.category,
                            fontSize = 9.sp,
                            color = ColorMutedSlate,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Top Inspector floating details overlay panel (tapped node detail)
        AnimatedVisibility(
            visible = inspectorDetailItem != null,
            enter = fadeIn() + expandVertically(),
            exit = shrinkVertically() + fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            inspectorDetailItem?.let { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ColorTacticalCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ColorSurfaceCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = item.classification.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorNeonAmber,
                                    letterSpacing = 2.sp,
                                    fontFamily = FontFamily.SansSerif
                                )
                                Text(
                                    text = item.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.White
                                )
                            }

                            IconButton(onClick = { inspectorDetailItem = null }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        Text(
                            text = item.description,
                            fontSize = 13.sp,
                            color = ColorMutedSlate,
                            lineHeight = 17.sp
                        )

                        // Admissibility details block
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ColorBackgroundBg, RoundedCornerShape(10.dp))
                                .border(1.dp, ColorTacticalCyan.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "LEGAL ADMISSIBILITY WARNING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTacticalCyan,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = item.admissibility,
                                fontSize = 11.sp,
                                color = Color.White,
                                lineHeight = 14.sp
                            )
                        }

                        // Collection action button toggle
                        Button(
                            onClick = {
                                viewModel.toggleEvidenceCollected(item.id)
                                // Update active list reference
                                inspectorDetailItem = item.copy(
                                    statusText = if (item.statusText == "FOUND") "MISSING" else "FOUND"
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (item.statusText == "FOUND") ColorBrightCoral else ColorCyberGreen
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (item.statusText == "FOUND") Icons.Default.Undo else Icons.Default.AddTask,
                                    contentDescription = "Toggle Collect",
                                    tint = ColorBackgroundBg,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (item.statusText == "FOUND") "MARK AS MISSING" else "MARK AS FOUND (COLLECT)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorBackgroundBg
                                )
                            }
                        }
                    }
                }
            }
        }

        // Top navigation back button row
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.background(ColorSurfaceCard, RoundedCornerShape(10.dp))
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = ColorTacticalCyan)
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = ColorSurfaceCard),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
            ) {
                Text(
                    text = "INVESTIGATION BOARD (ACTIVE)",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = ColorNeonAmber,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Box(modifier = Modifier.size(40.dp)) // Anchor balance spacer
        }
    }
}
