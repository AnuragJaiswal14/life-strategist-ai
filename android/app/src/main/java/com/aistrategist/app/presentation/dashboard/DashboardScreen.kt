package com.aistrategist.app.presentation.dashboard

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.aistrategist.app.domain.service.PomodoroService
import com.aistrategist.app.presentation.theme.glassmorphism
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
    onNavigateToStrategy: () -> Unit,
    onNavigateToForge: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val logs by viewModel.logs.collectAsState()
    val context = LocalContext.current
    val currentUser = viewModel.currentUser
    
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
                // Accordion Player Profile (Top)
                var isAccordionExpanded by remember { mutableStateOf(false) }
                val rotationAngle by animateFloatAsState(if (isAccordionExpanded) 180f else 0f)

                AnimatedVisibility(
                    visible = showRow1,
                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 4 }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassmorphism(RoundedCornerShape(24.dp))
                            .clickable { isAccordionExpanded = !isAccordionExpanded }
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = currentUser?.photoUrl ?: "https://api.dicebear.com/7.x/bottts/png?seed=ai",
                                    contentDescription = "Avatar",
                                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.DarkGray),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(currentUser?.displayName ?: "Strategist", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text(if (currentUser != null) "Verified Sync Mode" else "Local Instance", color = Color.White.copy(0.7f), style = MaterialTheme.typography.bodySmall)
                                }
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White, modifier = Modifier.rotate(rotationAngle))
                            }

                            // Expanded Features Accordion
                            AnimatedVisibility(visible = isAccordionExpanded) {
                                Column(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = onNavigateToStrategy, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))) {
                                        Text("Strategic Directions")
                                    }
                                    Button(onClick = onNavigateToForge, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))) {
                                        Text("Habit & Goal Forge")
                                    }
                                    Button(onClick = onNavigateToAudit, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))) {
                                        Text("Life Audit Lab")
                                    }
                                    Button(onClick = onNavigateToProfile, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                                        Text("Account Settings")
                                    }
                                }
                            }
                        }
                    }
                }

                // Chatbot Entry Module
                AnimatedVisibility(
                    visible = showRow1,
                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 4 }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .glassmorphism(RoundedCornerShape(24.dp), startColor = Color(0xFFEC4899).copy(0.4f), endColor = Color(0xFF8B5CF6).copy(0.4f))
                            .clickable { onNavigateToLog() }
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).background(Color.White.copy(0.2f), CircleShape), contentAlignment = Alignment.Center) {
                                Text("💬", style = MaterialTheme.typography.titleMedium)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Let's Talk🌟", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                // ROW 2: North Star Goal
                AnimatedVisibility(
                    visible = showRow2,
                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 4 }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
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
                            Spacer(modifier = Modifier.height(16.dp))
                            LinearProgressIndicator(
                                progress = 0.65f,
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFFFBBF24),
                                trackColor = Color.White.copy(0.1f)
                            )
                        }
                    }
                }

                // ROW 3: Strategic Snapshot -> Stretched to Full Width
                AnimatedVisibility(
                    visible = showRow3,
                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 4 }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassmorphism(RoundedCornerShape(24.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF34D399))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Strategic Snapshot", color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
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

                // ROW 4: Vitality Chart & Pomodoro Widget
                AnimatedVisibility(
                    visible = showRow4,
                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 4 }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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

                        // Pomodoro Intelligent Widget
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
                                        Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFFF97316))
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
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
