package com.aistrategist.app.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.entryModelOf

@Composable
fun DashboardScreen(
    onNavigateToLog: () -> Unit,
    onNavigateToReport: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val logs by viewModel.logs.collectAsState()
    
    val chartEntryModel = remember(logs) {
        val energyLevels = logs.map { it.energyLevel.toFloat() }.toTypedArray()
        if (energyLevels.isNotEmpty()) entryModelOf(*energyLevels) else entryModelOf(0f)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("AI Life Strategist Dashboard", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Recent Energy Levels", style = MaterialTheme.typography.titleMedium)
                ProvideChartStyle(m3ChartStyle()) {
                    Chart(
                        chart = columnChart(),
                        model = chartEntryModel,
                        startAxis = rememberStartAxis(),
                        bottomAxis = rememberBottomAxis(),
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = onNavigateToLog) {
                Text("Log Entry")
            }
            Button(onClick = onNavigateToReport) {
                Text("AI Report")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Recent Logs", style = MaterialTheme.typography.titleMedium)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(logs) { log ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Date: ${log.date}", style = MaterialTheme.typography.bodyLarge)
                        Text("Energy: ${log.energyLevel}/10")
                        Text("Habits: ${log.habitsJson}")
                    }
                }
            }
        }
    }
}
