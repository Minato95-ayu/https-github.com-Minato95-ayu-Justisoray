package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.SimulationViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScenarioSimulationScreen(
    viewModel: SimulationViewModel,
    onNavigateBack: () -> Unit
) {
    val selectedCase by viewModel.selectedCase.collectAsState()
    val currSlide = viewModel.currentSlide
    val frozen = viewModel.sceneFrozen
    val decision = viewModel.userDecision
    val revealed = viewModel.consequencesRevealed
    val blockText = viewModel.formalActionPlanText

    // Interactive slider variables
    val pulseAlpha by rememberInfiniteTransition(label = "Pulse").animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackgroundBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header bar
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
                        text = "LIVE SIMULATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (frozen) ColorBrightCoral else ColorCyberGreen,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = selectedCase?.title ?: "No Scenario Selected",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = { viewModel.resetCinemaProgress() },
                    modifier = Modifier.background(ColorSurfaceCard, RoundedCornerShape(10.dp))
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Simulation", tint = ColorNeonAmber)
                }
            }

            // Central Cinematic Frame Box with premium slide transitions
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(ColorSurfaceCard)
                    .border(
                        2.dp,
                        if (frozen) ColorBrightCoral.copy(alpha = pulseAlpha) else ColorTacticalCyan.copy(alpha = 0.2f),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                // Interactive freeze state stamp banner - Editorial style
                if (frozen) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                            .background(ColorNeonAmber, RoundedCornerShape(50))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color.Black, RoundedCornerShape(50))
                            )
                            Text(
                                text = "TIME FROZEN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Interactive Story Slide Renderer
                AnimatedContent(
                    targetState = currSlide,
                    transitionSpec = {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    label = "SlideContent"
                ) { targetSlide ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (targetSlide) {
                            1 -> {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(ColorTacticalCyan.copy(alpha = 0.12f), RoundedCornerShape(50))
                                        .border(2.dp, ColorTacticalCyan, RoundedCornerShape(50)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsWalk,
                                        contentDescription = "Step 1",
                                        tint = ColorTacticalCyan,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Scene 1: Entering the Precinct",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "You step into Sector 4 Police Substation clutching dual written copies of a severe physical assault complaint. A heavy ceiling fan hums silently as two officers converse nonchalantly at a corner desk.",
                                    fontSize = 14.sp,
                                    color = ColorMutedSlate,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                            2 -> {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(ColorNeonAmber.copy(alpha = 0.12f), RoundedCornerShape(50))
                                        .border(2.dp, ColorNeonAmber, RoundedCornerShape(50)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DoNotDisturb,
                                        contentDescription = "Step 2",
                                        tint = ColorNeonAmber,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Scene 2: Administrative Deflection",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "The Duty Officer glances through your documents. He shakes his head: 'This is related to private contractor labor transactions. It's a civil business contract matter, settle it at local forums. We do not register FIRs for simple civil arguments.'",
                                    fontSize = 14.sp,
                                    color = ColorMutedSlate,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                            3 -> {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(ColorBrightCoral.copy(alpha = 0.12f), RoundedCornerShape(50))
                                        .border(2.dp, ColorBrightCoral, RoundedCornerShape(50)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Pause,
                                        contentDescription = "Step 3",
                                        tint = ColorBrightCoral,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Scene 3: Time Freezes",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "The system has frozen the timeline. You are currently in an illegal administrative deadlock. In physical life, citizens immediately panic here, argue futilely, or accept defeat. We will analyze the underlying statutory rights.",
                                    fontSize = 14.sp,
                                    color = ColorMutedSlate,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                            4 -> {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(ColorCyberGreen.copy(alpha = 0.12f), RoundedCornerShape(50))
                                        .border(2.dp, ColorCyberGreen, RoundedCornerShape(50)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LockOpen,
                                        contentDescription = "Step 4",
                                        tint = ColorCyberGreen,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Scene 4: Rights Visualized",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "UNLOCKED: Section 154(1) CrPC (Mandatary First Information Report).\n\nIf physical injuries exist, it constitutes a 'cognizable offense' under law. The Supreme Court in Lalita Kumari v. UP enforced that officers do NOT have discretionary power; they MUST file an FIR immediately upon receipt of report.",
                                    fontSize = 14.sp,
                                    color = ColorMutedSlate,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            }
                            5 -> {
                                Text(
                                    text = "Scene 5: Multi-Branch Tactical Choice",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Select your response vector. Each path yields completely distinct legal consequences:",
                                    fontSize = 12.sp,
                                    color = ColorMutedSlate,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    BranchOptionCard(
                                        optionId = 1,
                                        title = "A) Escalate aggressively",
                                        summary = "Threaten officer with internal vigilance complaints on the spot.",
                                        isSelected = decision == 1,
                                        color = ColorBrightCoral,
                                        onClick = { viewModel.selectDecisionOption(1) }
                                    )

                                    BranchOptionCard(
                                        optionId = 2,
                                        title = "B) Demand Written Refusal Detail",
                                        summary = "Strictly request officer to write 'cognizable excuse' or stamp receipt of paper.",
                                        isSelected = decision == 2,
                                        color = ColorTacticalCyan,
                                        onClick = { viewModel.selectDecisionOption(2) }
                                    )

                                    BranchOptionCard(
                                        optionId = 3,
                                        title = "C) Route Sec 154(3) via Judicial post",
                                        summary = "Accept file copy, exit silently, and dispatch written copies to SP via Post.",
                                        isSelected = decision == 3,
                                        color = ColorCyberGreen,
                                        onClick = { viewModel.selectDecisionOption(3) }
                                    )
                                }
                            }
                            6 -> {
                                Text(
                                    text = "Scene 6: Visualizing Consequences",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                if (decision == null) {
                                    Text(
                                        text = "Please return to previous slide to make your selection vector.",
                                        fontSize = 14.sp,
                                        color = ColorMutedSlate,
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(ColorBackgroundBg, RoundedCornerShape(12.dp))
                                            .border(1.dp, ColorTacticalCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            text = when (decision) {
                                                1 -> "Tactical Consequence: High Friction Deadlock"
                                                2 -> "Tactical Consequence: Administrative Lever"
                                                3 -> "Tactical Consequence: Judicial Compliance Pathway"
                                                else -> ""
                                            },
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorNeonAmber,
                                            fontFamily = FontFamily.Monospace
                                        )

                                        Text(
                                            text = when (decision) {
                                                1 -> "Arguing creates situational excuses for officers to claim 'Obstructing public servant duties' under Sec 186 IPC. Avoid verbal threats. Act legally, silent and precise."
                                                2 -> "Demanding written refusals forces duty officers to reckon with documentation audits, usually prompting them to record the complaint in the Station General Diary (GD) to avoid litigation."
                                                3 -> "By dispatching the physical written complaint to the Superintendent (SP) via Registered Post (A.D), you establish concrete Section 154(3) administrative proof which unlocks Metropolitan Magistrate orders under Section 156(3) with ease."
                                                else -> ""
                                            },
                                            fontSize = 13.sp,
                                            color = Color.White,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                            7 -> {
                                Text(
                                    text = "Scene 7: Action Plan Generated",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "The platform has transformed your choice into a formal draft legal letter ready to be compiled:",
                                    fontSize = 12.sp,
                                    color = ColorMutedSlate,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .border(1.dp, ColorCyberGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                                    colors = CardDefaults.cardColors(containerColor = ColorBackgroundBg)
                                ) {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp)
                                    ) {
                                        item {
                                            Text(
                                                text = blockText.ifEmpty { "Perform action choices on prior slides to draft legal document notices." },
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = ColorCyberGreen,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }
                            }
                            8 -> {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(ColorCyberGreen.copy(alpha = 0.12f), RoundedCornerShape(50))
                                        .border(2.dp, ColorCyberGreen, RoundedCornerShape(50)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Success",
                                        tint = ColorCyberGreen,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Immersive Law Experience Concomplete!",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "You have unlocked the vital, tactical legal levers behind filing police FIR complaints.\n\nContinue monitoring your progress on the live case Timeline or inspect gathered artifacts inside the tactile investigation board sandbox.",
                                    fontSize = 13.sp,
                                    color = ColorMutedSlate,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Progress bar showing simulation controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Previous button
                Button(
                    onClick = {
                        if (viewModel.currentSlide > 1) {
                            viewModel.nextSlide() // (viewModel doesn't support prev, we'll configure back reset)
                            // Basic decrement workaround
                            viewModel.resetCinemaProgress()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorSurfaceCard),
                    modifier = Modifier.border(1.dp, ColorTacticalCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Restart", color = ColorTacticalCyan, fontSize = 11.sp)
                }

                // Slide locator indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..8) {
                        Box(
                            modifier = Modifier
                                .size(if (i == currSlide) 14.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (i == currSlide) {
                                        if (frozen) ColorBrightCoral else ColorTacticalCyan
                                    } else {
                                        ColorMutedSlate.copy(alpha = 0.4f)
                                    }
                                )
                        )
                    }
                }

                // Next Slide action button
                Button(
                    onClick = { viewModel.nextSlide() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (frozen) ColorBrightCoral else ColorTacticalCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = currSlide < 8
                ) {
                    Text(
                        text = if (currSlide == 5 && decision == null) "Select Choice" else "Proceed",
                        color = ColorBackgroundBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BranchOptionCard(
    optionId: Int,
    title: String,
    summary: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                1.dp,
                if (isSelected) color else color.copy(alpha = 0.15f),
                RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ColorBackgroundBg else ColorSurfaceCard
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(2.dp, color, RoundedCornerShape(50))
                    .background(
                        if (isSelected) color else Color.Transparent,
                        RoundedCornerShape(50)
                    )
            )

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = summary,
                    fontSize = 11.sp,
                    color = ColorMutedSlate,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
