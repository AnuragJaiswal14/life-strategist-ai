package com.aistrategist.app.presentation.report

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.aistrategist.app.presentation.theme.glassmorphism
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    onBack: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    // The Ripple State
    var isCommitting by remember { mutableStateOf(false) }
    val rippleRadius = remember { Animatable(0f) }
    val rippleAlpha = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    var buttonCenter by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))) { // Dark Indigo Base
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // App Bar Overlay
            TopAppBar(
                title = { Text("Strategic Consultant", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Course Correction Block
                Box(modifier = Modifier.fillMaxWidth().glassmorphism(RoundedCornerShape(24.dp)).padding(20.dp)) {
                    Column {
                        Text("Course Correction", color = Color(0xFFF43F5E), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Stop:", color = Color.White.copy(0.7f), fontWeight = FontWeight.Bold)
                        Text("Scrolling Twitter immediately upon waking up.", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Start:", color = Color.White.copy(0.7f), fontWeight = FontWeight.Bold)
                        Text("Allocating the first 50 minutes strictly to the North Star MVP.", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Continue:", color = Color.White.copy(0.7f), fontWeight = FontWeight.Bold)
                        Text("Hitting your 8,000 steps baseline in the evening.", color = Color.White)
                    }
                }

                // Pivot Logic Block
                Box(modifier = Modifier.fillMaxWidth().glassmorphism(RoundedCornerShape(24.dp)).padding(20.dp)) {
                    Column {
                        Text("Pivot Logic Analytics", color = Color(0xFF34D399), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "My analysis shows an 80% correlation between high morning social media usage and afternoon burnout. By shifting focus entirely to the MVP immediately, your Attention ROI will multiply.",
                            color = Color.White.copy(0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // The Commitment Action
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .glassmorphism(RoundedCornerShape(36.dp), startColor = Color(0xFF10B981).copy(alpha = 0.4f), endColor = Color(0xFF059669).copy(0.1f))
                        .onGloballyPositioned { coordinates ->
                            val pos = coordinates.positionInRoot()
                            val size = coordinates.size.toSize()
                            buttonCenter = Offset(pos.x + size.width / 2, pos.y + size.height / 2)
                        }
                        .clickable(enabled = !isCommitting) {
                            isCommitting = true
                            coroutineScope.launch {
                                // Execute the massive full-screen Ripple expansion
                                rippleRadius.animateTo(
                                    targetValue = 2500f,
                                    animationSpec = tween(durationMillis = 800)
                                )
                                rippleAlpha.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(durationMillis = 400)
                                )
                                onBack() // Send them back automatically!
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("I Commit to this Strategy", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        // Overlay Canvas for the Ripple Haptic Effect
        if (isCommitting) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0xFF10B981).copy(alpha = rippleAlpha.value),
                    radius = rippleRadius.value,
                    center = buttonCenter
                )
            }
        }
    }
}
