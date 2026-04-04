package com.aistrategist.app.presentation.audit

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import com.aistrategist.app.presentation.theme.glassmorphism
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter

fun hasUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.unsafeCheckOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditScreen(
    onBack: () -> Unit,
    onNavigateToStrategy: (String?) -> Unit,
    viewModel: AuditViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(hasUsageStatsPermission(context)) }
    val usageData by viewModel.usageData.collectAsState()
    val energyRoiData by viewModel.energyRoiData.collectAsState()
    
    val lifecycleOwner = LocalLifecycleOwner.current

    var selectedDays by remember { mutableStateOf(7) }
    var expanded by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val check = hasUsageStatsPermission(context)
                hasPermission = check
                if (check) viewModel.loadUsageData(selectedDays)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Dynamic Top Application Label Extraction
    val appNames = remember(usageData) { usageData?.map { it.appName } ?: emptyList() }
    val attentionHeatmapModel = remember(usageData) {
        if (usageData != null && usageData!!.isNotEmpty()) {
            val hourValues = usageData!!.map { it.timeInHours }.toTypedArray()
            entryModelOf(*hourValues)
        } else {
            entryModelOf(0f)
        }
    }

    val bottomAxisFormatter = AxisValueFormatter<com.patrykandpatrick.vico.core.axis.AxisPosition.Horizontal.Bottom> { value, _ ->
        val index = value.toInt()
        val rawName = appNames.getOrNull(index) ?: ""
        if (rawName.length > 8) rawName.take(6) + ".." else rawName
    }

    // Chronometric 24-Hour OS Idle Energy Mapping
    val roiModel = remember(energyRoiData) {
        if (energyRoiData != null && energyRoiData!!.isNotEmpty()) {
            entryModelOf(*energyRoiData!!.toTypedArray())
        } else {
            entryModelOf(0f)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            TopAppBar(
                title = { Text("Life Audit Lab", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // The Interactive Attention Heatmap
                Box(modifier = Modifier.fillMaxWidth().glassmorphism(RoundedCornerShape(24.dp)).padding(20.dp)) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically, 
                            modifier = Modifier.fillMaxWidth(), 
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Map, contentDescription = null, tint = Color(0xFF3B82F6))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Attention Flow", color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)
                            }
                            
                            // Dynamic Timeframe Selector Dropdown
                            Box {
                                TextButton(onClick = { expanded = true }) {
                                    Text("Last $selectedDays Days", color = Color.White, style = MaterialTheme.typography.labelMedium)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                                }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    DropdownMenuItem(text = { Text("Last 2 Days") }, onClick = { selectedDays = 2; expanded = false; viewModel.loadUsageData(selectedDays) })
                                    DropdownMenuItem(text = { Text("Last 7 Days (Default)") }, onClick = { selectedDays = 7; expanded = false; viewModel.loadUsageData(selectedDays) })
                                    DropdownMenuItem(text = { Text("Last 15 Days") }, onClick = { selectedDays = 15; expanded = false; viewModel.loadUsageData(selectedDays) })
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (!hasPermission) {
                            Column(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Usage Stats Required", color = Color.White, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("We need deep system access to map your focus leaks from the Android OS.", color = Color.White.copy(0.7f), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        })
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                                ) {
                                    Text("Grant Extraction Access")
                                }
                            }
                        } else if (usageData == null) {
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFF3B82F6))
                            }
                        } else {
                            // Column Chart Projection
                            ProvideChartStyle(m3ChartStyle()) {
                                Chart(
                                    chart = columnChart(),
                                    model = attentionHeatmapModel,
                                    startAxis = rememberStartAxis(),
                                    bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisFormatter),
                                    modifier = Modifier.fillMaxWidth().height(200.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Interactive Legend Ribbon (Chatbot Aesthetic)
                            Text("Interactive Breakdown 🌟", color = Color.White, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                usageData?.forEach { app ->
                                    Box(
                                        modifier = Modifier
                                            .glassmorphism(
                                                RoundedCornerShape(16.dp), 
                                                startColor = Color(0xFFEC4899).copy(0.4f), 
                                                endColor = Color(0xFF8B5CF6).copy(0.4f)
                                            )
                                            .clickable { onNavigateToStrategy(app.appName) }
                                            .padding(horizontal = 16.dp, vertical = 10.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(app.appName, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                            Text(String.format("%.1fh", app.timeInHours), color = Color.White.copy(0.8f), style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tap any app above to formulate a reclaim strategy.", color = Color.White.copy(0.5f), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                // Reverse Systemic Energy ROI Chart (24 Hour Idle Track)
                Box(modifier = Modifier.fillMaxWidth().height(300.dp).glassmorphism(RoundedCornerShape(24.dp)).padding(20.dp)) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Bolt, contentDescription = null, tint = Color(0xFFFBBF24))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Energy Restorative Index (24h)", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (energyRoiData == null && hasPermission) {
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFFFBBF24))
                            }
                        } else {
                            ProvideChartStyle(m3ChartStyle()) {
                                Chart(
                                    chart = lineChart(),
                                    model = roiModel,
                                    startAxis = rememberStartAxis(),
                                    bottomAxis = rememberBottomAxis(),
                                    modifier = Modifier.fillMaxWidth().height(200.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Tracking systemic device idle-states as positive human restoration multipliers.", color = Color.White.copy(0.6f), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }

                // AI Waste Identification (Red Zones)
                Box(modifier = Modifier.fillMaxWidth().glassmorphism(RoundedCornerShape(24.dp), startColor = Color.Red.copy(0.1f), endColor = Color.Red.copy(0.05f)).padding(20.dp)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Identified Energy Leaks", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "• Tuesday Evening: 2 hours of unstructured browsing after high-cognitive load tasks.\n• Thursday Afternoon: Severe energy crash (rated 2/10) following back-to-back meetings without physical movement.",
                            color = Color.White.copy(0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onNavigateToStrategy(null) },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Formulate Weekly Strategy", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
