package com.aistrategist.app.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.aistrategist.app.domain.service.PomodoroService
import com.aistrategist.app.presentation.theme.glassmorphism
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import kotlinx.coroutines.delay
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.entryModelOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToLog: () -> Unit,
    onNavigateToAudit: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val logs by viewModel.logs.collectAsState()
    val context = LocalContext.current
    
    // Bind directly to Foreground Service Flow (safe for UI)
    val isTimerRunning by PomodoroService.isRunning.collectAsState()
    val timerMillis by PomodoroService.timeLeftInMillis.collectAsState()
    
    // Auto-calculate interval
    val energyLevel = logs.lastOrNull()?.energyLevel ?: 5
    val optimalInterval = PomodoroService.calculateOptimalInterval(energyLevel)

    val chartEntryModel = remember(logs) {
        val energyLevels = logs.takeLast(7).map { it.energyLevel.toFloat() }.toTypedArray()
        if (energyLevels.isNotEmpty()) entryModelOf(*energyLevels) else entryModelOf(4f, 7f, 5f, 9f, 8f) // Stub for preview
    }

    // Staggered Entrance Animation States
    var showRow1 by remember { mutableStateOf(false) }
    var showRow2 by remember { mutableStateOf(false) }
    var showRow3 by remember { mutableStateOf(false) }
    var showRow4 by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150)
        showRow1 = true
        delay(150)
        showRow2 = true
        delay(150)
        showRow3 = true
        delay(150)
        showRow4 = true
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))) { // Dark Indigo Background
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Text(
                        "Command Center",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .glassmorphism(CircleShape, startColor = Color.White.copy(0.2f), endColor = Color.White.copy(0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            // Scrollable Bento Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ROW 1: North Star Goal
                AnimatedVisibility(
                    visible = showRow1,
                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 4 }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .glassmorphism(RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("The North Star", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Launch the MVP and secure 10 initial beta users by Friday.",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            LinearProgressIndicator(
                                progress = 0.65f,
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFFFBBF24),
                                trackColor = Color.White.copy(0.1f)
                            )
                        }
                    }
                }

                // ROW 2: Strategic Snapshot & Quick Log
                AnimatedVisibility(
                    visible = showRow2,
                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 4 }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left Square: Quick-Log
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .glassmorphism(RoundedCornerShape(24.dp), startColor = Color(0xFF3B82F6).copy(0.3f), endColor = Color(0xFF2563EB).copy(0.1f))
                                .clickable { onNavigateToLog() } // Navigates to Pulse Chat
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Log Drain", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("or Habit", color = Color.White.copy(0.7f), style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        // Right Rectangle: Snapshot
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                                .glassmorphism(RoundedCornerShape(24.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF34D399))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Snapshot", color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Your energy bottoms out heavily after 3 PM. You are spending excessive time on non-critical tasks. Recommend a 20-min recharge walk today.",
                                    color = Color.White.copy(0.9f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                                )
                            }
                        }
                    }
                }

                // ROW 3: Life Vitality Chart Mini-Preview
                AnimatedVisibility(
                    visible = showRow3,
                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 4 }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .glassmorphism(RoundedCornerShape(24.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFFA78BFA))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Vitality Flow", color = Color(0xFFA78BFA), fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            ProvideChartStyle(m3ChartStyle()) {
                                Chart(
                                    chart = lineChart(),
                                    model = chartEntryModel,
                                    startAxis = rememberStartAxis(),
                                    bottomAxis = rememberBottomAxis(),
                                )
                            }
                        }
                    }
                }

                // Pomodoro Intelligent Widget & Button
                AnimatedVisibility(
                    visible = showRow4,
                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 4 }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .glassmorphism(RoundedCornerShape(24.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFF97316))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Optimal Focus State", color = Color(0xFFF97316), fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        if (isTimerRunning) {
                                            val m = (timerMillis / 1000) / 60
                                            val s = (timerMillis / 1000) % 60
                                            String.format("%02d:%02d remaining", m, s)
                                        } else {
                                            "AI Suggests: ${optimalInterval}m based on Energy"
                                        },
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                
                                IconButton(
                                    onClick = {
                                        val intent = Intent(context, PomodoroService::class.java)
                                        if (isTimerRunning) {
                                            intent.action = PomodoroService.ACTION_STOP
                                            context.startService(intent) // stopService can just use standard intent
                                        } else {
                                            intent.action = PomodoroService.ACTION_START
                                            intent.putExtra(PomodoroService.EXTRA_DURATION_MINS, optimalInterval)
                                            ContextCompat.startForegroundService(context, intent)
                                        }
                                    },
                                    modifier = Modifier.background(if (isTimerRunning) Color.Red.copy(0.8f) else Color(0xFFF97316), CircleShape)
                                ) {
                                    Icon(
                                        if (isTimerRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        // Launch Full Audit Lab Action
                        Button(
                            onClick = onNavigateToAudit,
                            modifier = Modifier.fillMaxWidth().height(64.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Enter Audit Lab", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
