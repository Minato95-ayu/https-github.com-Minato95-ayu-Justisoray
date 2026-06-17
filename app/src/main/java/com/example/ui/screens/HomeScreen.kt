package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CaseEntity
import com.example.ui.viewmodel.SimulationViewModel

// Editorial Aesthetic styling palette constants
val ColorBackgroundBg = Color(0xFF050505)
val ColorSurfaceCard = Color(0xFF0F0F0F)
val ColorTacticalCyan = Color(0xFF6366F1) // Indigo/Tactical Accent
val ColorCyberGreen = Color(0xFF10B981) // Editorial Emerald/Green
val ColorNeonAmber = Color(0xFFF59E0B) // Editorial Warm Amber Highlight
val ColorMutedSlate = Color(0xFF94A3B8) // High-elegance Slate gray
val ColorBrightCoral = Color(0xFFEF4444) // Crisp Editorial Coral/Red

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: SimulationViewModel,
    onNavigateToScenario: () -> Unit,
    onNavigateToTimeline: () -> Unit,
    onNavigateToInvestigation: () -> Unit
) {
    val cases by viewModel.casesState.collectAsState()
    val selectedCase by viewModel.selectedCase.collectAsState()

    // Decorative grid/matrix lines drawn in background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackgroundBg)
            .drawBehind {
                val step = 100.dp.toPx()
                var currentX = 0f
                while (currentX < size.width) {
                    drawLine(
                        color = ColorTacticalCyan.copy(alpha = 0.04f),
                        start = Offset(currentX, 0f),
                        end = Offset(currentX, size.height),
                        strokeWidth = 1f
                    )
                    currentX += step
                }
                var currentY = 0f
                while (currentY < size.height) {
                    drawLine(
                        color = ColorTacticalCyan.copy(alpha = 0.04f),
                        start = Offset(0f, currentY),
                        end = Offset(size.width, currentY),
                        strokeWidth = 1f
                    )
                    currentY += step
                }
            }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Branding Section - Editorial Aesthetic
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "INVESTIGATION LIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorNeonAmber,
                            letterSpacing = 2.5.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Justisoray",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Ambient glowing dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(ColorNeonAmber, RoundedCornerShape(50))
                        )
                        IconButton(
                            onClick = { viewModel.resetCinemaProgress() },
                            modifier = Modifier
                                .background(ColorSurfaceCard, RoundedCornerShape(12.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = "Justice Symbol",
                                tint = ColorNeonAmber
                            )
                        }
                    }
                }
            }

            // Visual 3D environments slider
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "3D Legal Universe",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Interactive Environments",
                            fontSize = 11.sp,
                            color = ColorMutedSlate
                        )
                    }
                    
                    UniverseSliderSection(
                        selectedEnv = selectedCase?.environment ?: "POLICE_STATION",
                        onSelectEnvironment = { env ->
                            // Auto select corresponding case if matches default
                            val matchingCase = cases.find { it.environment == env }
                            if (matchingCase != null) {
                                viewModel.selectCase(matchingCase.id)
                            } else {
                                // Create temporary offline template case
                                // (keeps user anchored to active environments flawlessly)
                            }
                        }
                    )
                }
            }

            // Active Case Dossier controls
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ColorTacticalCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ColorSurfaceCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(ColorCyberGreen, RoundedCornerShape(50))
                                )
                                Text(
                                    text = "ACTIVE SIMULATION LOADOUT",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorCyberGreen,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            
                            if (selectedCase != null) {
                                IconButton(
                                    onClick = { viewModel.deleteCurrentCase() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Loadout",
                                        tint = ColorBrightCoral,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        if (selectedCase != null) {
                            Text(
                                text = selectedCase!!.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = selectedCase!!.summary,
                                fontSize = 13.sp,
                                color = ColorMutedSlate,
                                lineHeight = 18.sp
                            )

                            // Interactive Case readiness parameters
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Case Conviction Readiness",
                                        fontSize = 11.sp,
                                        color = ColorMutedSlate
                                    )
                                    Text(
                                        text = "${selectedCase!!.confidence}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorTacticalCyan,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { selectedCase!!.confidence / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (selectedCase!!.confidence < 45) ColorBrightCoral else ColorCyberGreen,
                                    trackColor = Color.White.copy(alpha = 0.1f)
                                )
                            }

                            // Dynamic Visual Cinematic Navigation controls
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                maxItemsInEachRow = 3
                            ) {
                                Button(
                                    onClick = onNavigateToScenario,
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorNeonAmber),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Scenario Icon",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Scenario", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorBackgroundBg)
                                }

                                Button(
                                    onClick = onNavigateToTimeline,
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorSurfaceCard),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.dp, ColorTacticalCyan.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timeline,
                                        contentDescription = "Timeline Icon",
                                        tint = ColorTacticalCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Timeline", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorTacticalCyan)
                                }

                                Button(
                                    onClick = onNavigateToInvestigation,
                                    colors = ButtonDefaults.buttonColors(containerColor = ColorSurfaceCard),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.dp, ColorCyberGreen.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Grid4x4,
                                        contentDescription = "Investigate Board Icon",
                                        tint = ColorCyberGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Board", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorCyberGreen)
                                }
                            }
                        } else {
                            Text(
                                text = "No active dossier loaded. Use the AI engine below to map your dynamic custom legal scenario or tap an environment above!",
                                fontSize = 13.sp,
                                color = ColorMutedSlate,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )
                        }
                    }
                }
            }

            // AI dynamic legal generator workspace
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "AI Legal Reasoner Engine",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, ColorNeonAmber.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = ColorSurfaceCard),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Describe any real-life dispute (e.g. cyber theft, landlord deposit refusal, wage withholding, police non-cooperation). The system will reason, extract statutory sections, connect evidence requirements, and blueprint your timeline.",
                                fontSize = 12.sp,
                                color = ColorMutedSlate,
                                lineHeight = 16.sp
                            )

                            OutlinedTextField(
                                value = viewModel.inputPrompt,
                                onValueChange = { viewModel.inputPrompt = it },
                                label = { Text("E.g., Landlord has withheld my security deposit...", color = ColorMutedSlate) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ColorTacticalCyan,
                                    unfocusedBorderColor = ColorMutedSlate.copy(alpha = 0.4f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                maxLines = 4,
                                shape = RoundedCornerShape(12.dp)
                            )

                            if (viewModel.aiErrorText != null) {
                                Text(
                                    text = viewModel.aiErrorText!!,
                                    color = ColorNeonAmber,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Button(
                                onClick = { viewModel.submitCustomDispute() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = viewModel.inputPrompt.isNotEmpty() && !viewModel.isLoadingAI,
                                colors = ButtonDefaults.buttonColors(containerColor = ColorTacticalCyan),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (viewModel.isLoadingAI) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = ColorBackgroundBg
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("AI Reasoning in Progress...", fontSize = 12.sp, color = ColorBackgroundBg, fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI Generate",
                                        tint = ColorBackgroundBg,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("CONSTRUCT EXPERIENCE BOARD", fontSize = 12.sp, color = ColorBackgroundBg, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Dossiers catalog list
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Historical Case Files",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    if (cases.isEmpty()) {
                        Text(
                            text = "No cases currently tracked. Describe one to populate immediately.",
                            fontSize = 12.sp,
                            color = ColorMutedSlate
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(cases) { c ->
                                val isSelected = c.id == selectedCase?.id
                                Card(
                                    modifier = Modifier
                                        .width(220.dp)
                                        .clickable { viewModel.selectCase(c.id) }
                                        .border(
                                            1.dp,
                                            if (isSelected) ColorTacticalCyan else ColorTacticalCyan.copy(alpha = 0.1f),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    colors = CardDefaults.cardColors(containerColor = if (isSelected) ColorSurfaceCard.copy(alpha = 0.8f) else ColorSurfaceCard),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = c.environment.replace("_", " "),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorNeonAmber,
                                            fontFamily = FontFamily.Monospace
                                        )

                                        Text(
                                            text = c.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1
                                        )

                                        Text(
                                            text = c.summary,
                                            fontSize = 11.sp,
                                            color = ColorMutedSlate,
                                            maxLines = 2,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// Visual isometric micro-theme cards representing Environments list
data class Environment3D(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val tint: Color
)

@Composable
fun UniverseSliderSection(
    selectedEnv: String,
    onSelectEnvironment: (String) -> Unit
) {
    val environments = remember {
        listOf(
            Environment3D("POLICE_STATION", "Police Station", "Complaint registration to FIR lockups", Icons.Default.Security, ColorTacticalCyan),
            Environment3D("COURTROOM", "Judicial Judiciary Court", "Filing chargesheets to active appeals", Icons.Default.Gavel, ColorNeonAmber),
            Environment3D("CYBER_CRIME", "Cyber Crime Center", "Digital extortion & network frauds", Icons.Default.Monitor, ColorCyberGreen),
            Environment3D("CONSUMER_FORUM", "Consumer Forum", "Merchant fraud and package defaults", Icons.Default.ShoppingBag, ColorTacticalCyan),
            Environment3D("LABOUR_OFFICE", "Labour Commisioner Office", "Employer wage default and workplace bias", Icons.Default.Work, ColorNeonAmber),
            Environment3D("FAMILY_COURT", "Family Arbitrage Court", "Mutual settlements and contract division", Icons.Default.People, ColorCyberGreen)
        )
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(environments) { env ->
            val isSelected = env.id == selectedEnv
            
            // Isometric hover/depth projection effect in Compose!
            val scale by animateFloatAsState(if (isSelected) 1.05f else 0.95f, label = "Scale")
            val transY by animateFloatAsState(if (isSelected) -4f else 0f, label = "TransY")
            val cardElevation by animateDpAsState(if (isSelected) 8.dp else 2.dp, label = "Elevation")

            Card(
                modifier = Modifier
                    .width(160.dp)
                    .height(180.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationY = transY
                        rotationY = if (isSelected) -8f else 5f // isometric rotate perspective
                        rotationX = if (isSelected) 5f else 0f
                        cameraDistance = 8 * density
                    }
                    .clickable { onSelectEnvironment(env.id) }
                    .border(
                        1.dp,
                        if (isSelected) env.tint else env.tint.copy(alpha = 0.15f),
                        RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ColorSurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Isometric blueprint styling grid on the card face
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cardStep = 18.dp.toPx()
                        var x = 0f
                        while (x < size.width) {
                            drawLine(
                                color = env.tint.copy(alpha = 0.05f),
                                start = Offset(x, 0f),
                                end = Offset(x + size.height, size.height)
                            )
                            x += cardStep
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(env.tint.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .border(1.dp, env.tint.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = env.icon,
                                contentDescription = env.name,
                                tint = env.tint,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = env.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = env.description,
                                fontSize = 11.sp,
                                color = ColorMutedSlate,
                                lineHeight = 14.sp,
                                maxLines = 3
                            )
                        }
                    }
                    
                    // Small neon locator pulse indicator
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .size(8.dp)
                                .background(env.tint, RoundedCornerShape(50))
                        )
                    }
                }
            }
        }
    }
}
