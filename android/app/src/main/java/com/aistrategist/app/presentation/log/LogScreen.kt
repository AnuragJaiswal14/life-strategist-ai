package com.aistrategist.app.presentation.log

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LogScreen(
    onBack: () -> Unit,
    viewModel: LogViewModel = hiltViewModel()
) {
    val energyLevel by viewModel.energyLevel.collectAsState()
    var habits by remember { mutableStateOf("") }
    var timeSpent by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Daily Log", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Energy Level: ${energyLevel.toInt()}/10")
        Slider(
            value = energyLevel,
            onValueChange = viewModel::setEnergyLevel,
            valueRange = 1f..10f,
            steps = 8
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = habits,
            onValueChange = { habits = it },
            label = { Text("Habits Completed (e.g. Exercise)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = timeSpent,
            onValueChange = { timeSpent = it },
            label = { Text("Time Spent (e.g. Work: 8h, Social: 2h)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) {
                Text("Cancel")
            }
            Button(onClick = { viewModel.submitLog(habits, timeSpent, onBack) }) {
                Text("Save Log")
            }
        }
    }
}
