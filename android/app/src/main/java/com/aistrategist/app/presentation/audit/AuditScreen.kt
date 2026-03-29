package com.aistrategist.app.presentation.audit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aistrategist.app.presentation.theme.glassmorphism
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.entryModelOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditScreen(
    onBack: () -> Unit,
    onNavigateToStrategy: () -> Unit
) {
    // Dummy Data for charts
    // Energy ROI compares [Time Spent in Flow] vs [Energy Maintained]
    val roiModel = remember { entryModelOf(4f, 7f, 5f, 9f, 8f, 6f, 9f) }
    
    // Using a stacked/column chart as a mock "Heatmap" representation of attention segments 
    // across days (e.g. Day 1: 2h Deep Work, Day 2: 5h Deep Work)
    val attentionHeatmapModel = remember { entryModelOf(1f, 3f, 5f, 2f, 4f, 6f, 3f) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))) { // Dark Indigo Base
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
                // The Attention Heatmap
                Box(modifier = Modifier.fillMaxWidth().height(260.dp).glassmorphism(RoundedCornerShape(24.dp)).padding(20.dp)) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Map, contentDescription = null, tint = Color(0xFF3B82F6))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Attention Distribution", color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        ProvideChartStyle(m3ChartStyle()) {
                            Chart(
                                chart = columnChart(),
                                model = attentionHeatmapModel,
                                startAxis = rememberStartAxis(),
                                bottomAxis = rememberBottomAxis(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Energy ROI Chart
                Box(modifier = Modifier.fillMaxWidth().height(260.dp).glassmorphism(RoundedCornerShape(24.dp)).padding(20.dp)) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Bolt, contentDescription = null, tint = Color(0xFFFBBF24))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Energy ROI", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        ProvideChartStyle(m3ChartStyle()) {
                            Chart(
                                chart = lineChart(),
                                model = roiModel,
                                startAxis = rememberStartAxis(),
                                bottomAxis = rememberBottomAxis(),
                                modifier = Modifier.weight(1f)
                            )
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

                // Navigate to Strategic Planning
                Button(
                    onClick = onNavigateToStrategy,
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
