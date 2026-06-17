package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TimelineItemEntity
import com.example.ui.viewmodel.SimulationViewModel

@Composable
fun TimelineScreen(
    viewModel: SimulationViewModel,
    onNavigateBack: () -> Unit
) {
    val selectedCase by viewModel.selectedCase.collectAsState()
    val timelineRecords by viewModel.activeTimeline.collectAsState()

    var activeFilter by remember { mutableStateOf("ALL") } // "ALL", "PAST", "PRESENT", "FUTURE"

    val filteredRecords = remember(timelineRecords, activeFilter) {
        when (activeFilter) {
            "PAST" -> timelineRecords.filter { it.type == "EVENT" }
            "PRESENT" -> timelineRecords.filter { it.type == "RIGHT" || it.type == "EVIDENCE" }
            "FUTURE" -> timelineRecords.filter { it.type == "PROCEDURE" || it.type == "ACTION" }
            else -> timelineRecords
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackgroundBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.background(ColorSurfaceCard, RoundedCornerShape(10.dp))
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = ColorTacticalCyan)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "STATUTORY DOSSIER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorNeonAmber,
                        letterSpacing = 2.5.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = selectedCase?.title ?: "Multi-Layer Timeline",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        color = Color.White
                    )
                }

                Box(modifier = Modifier.size(40.dp)) // Anchor balance spacer
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Layer/chronological filters row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "PAST", "PRESENT", "FUTURE").forEach { filter ->
                    val isSelected = filter == activeFilter
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeFilter = filter }
                            .background(
                                if (isSelected) ColorTacticalCyan.copy(alpha = 0.12f) else ColorSurfaceCard,
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) ColorTacticalCyan else ColorTacticalCyan.copy(alpha = 0.1f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filter,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) ColorTacticalCyan else ColorMutedSlate,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredRecords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = "Empty Timelines",
                            tint = ColorMutedSlate,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No timeline logs tracked in this category.",
                            fontSize = 13.sp,
                            color = ColorMutedSlate
                        )
                    }
                }
            } else {
                // Interactive chronological list mapping nodes
                Box(modifier = Modifier.weight(1f)) {
                    // Custom Draw background coordinate path line (glowing timeline track)
                    Canvas(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(42.dp)
                            .align(Alignment.TopStart)
                            .padding(start = 20.dp)
                    ) {
                        drawLine(
                            color = ColorTacticalCyan.copy(alpha = 0.15f),
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 6f
                        )
                        drawLine(
                            color = ColorTacticalCyan.copy(alpha = 0.5f),
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 1.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        itemsIndexed(filteredRecords) { index, item ->
                            TimelineVisualNodeCard(index + 1, item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineVisualNodeCard(
    displayIdx: Int,
    item: TimelineItemEntity
) {
    var expanded by remember { mutableStateOf(false) }

    // Theme pairing styles for Node layers
    val (nodeColor, icon) = remember(item.type) {
        when (item.type) {
            "EVENT" -> Pair(ColorMutedSlate, Icons.Default.Event)
            "RIGHT" -> Pair(ColorCyberGreen, Icons.Default.FactCheck)
            "EVIDENCE" -> Pair(ColorTacticalCyan, Icons.Default.Grid4x4)
            "PROCEDURE" -> Pair(ColorNeonAmber, Icons.Default.Shield)
            "ACTION" -> Pair(ColorBrightCoral, Icons.Default.TrendingUp)
            else -> Pair(ColorTacticalCyan, Icons.Default.Gavel)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Glowing visual connector point on the track line
        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .size(16.dp)
                .border(2.dp, nodeColor, RoundedCornerShape(50))
                .background(ColorBackgroundBg, RoundedCornerShape(50))
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(nodeColor, RoundedCornerShape(50))
            )
        }

        // Action details panel card
        Card(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, nodeColor.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = ColorSurfaceCard),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = item.type,
                            tint = nodeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = item.relativeTime,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = nodeColor,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(nodeColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.type,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = nodeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    color = ColorMutedSlate,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Render dynamic statutory sources or section citations
                if (item.lawSource != null) {
                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .background(ColorBackgroundBg, RoundedCornerShape(8.dp))
                            .border(1.dp, ColorNeonAmber.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Legal Stat",
                                tint = ColorNeonAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "CITATION: ${item.lawSource}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = ColorNeonAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
