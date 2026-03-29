package com.aistrategist.app.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistrategist.app.presentation.theme.EnergyParticleCanvas
import com.aistrategist.app.presentation.theme.glassmorphism
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.aistrategist.app.domain.repository.LogRepository
import com.aistrategist.app.data.remote.dto.ChatMessageDto

data class ChatMessage(val content: String, val isUser: Boolean, val isLoading: Boolean = false)

@HiltViewModel
class PulseViewModel @Inject constructor(
    private val logRepository: LogRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("Hello. I'm your Strategist. I noticed your energy dipped at 2 PM yesterday. How are you allocating your time today?", isUser = false)
        )
    )
    val messages = _messages.asStateFlow()

    // Energy level state derived from AI analytics (Mocked for UI visualization)
    private val _energyLevel = MutableStateFlow(8) // Default to High Focus
    val energyLevel = _energyLevel.asStateFlow()

    fun sendMessage(text: String) {
        val currentList = _messages.value.toMutableList()
        currentList.add(ChatMessage(text, isUser = true))
        // Add stub for loading response
        currentList.add(ChatMessage("...", isUser = false, isLoading = true))
        _messages.value = currentList
        
        // Simulating Backend Network Call & API response stream
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500) // Fake processing time
            val aiResponseList = _messages.value.toMutableList()
            aiResponseList.removeLast() // Remove loading stub
            
            // If user mentions "tired" or "burnout", trigger real-time particle shift locally immediately
            if (text.contains("tired", ignoreCase = true) || text.contains("burnout", ignoreCase = true)) {
                _energyLevel.value = 3 
            } else if (text.contains("focused", ignoreCase = true)) {
                _energyLevel.value = 9
            }

            // Sync with NestJS Backend
            val messageHistory = aiResponseList.mapNotNull {
                if (it.content == "...") null 
                else ChatMessageDto(role = if (it.isUser) "user" else "assistant", content = it.content)
            }
            
            val result = logRepository.sendChatMessage(messageHistory)
            
            result.onSuccess { response ->
                aiResponseList.add(ChatMessage(response.message, isUser = false))
                _messages.value = aiResponseList
            }.onFailure { e ->
                aiResponseList.add(ChatMessage("My connection to the neural lattice is unstable... (${e.message})", isUser = false))
                _messages.value = aiResponseList
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulseScreen(
    onNavigateToDashboard: () -> Unit,
    viewModel: PulseViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val energyLevel by viewModel.energyLevel.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))) { // Dark Indigo Base
        // Background Reactive Particles Layer
        EnergyParticleCanvas(
            modifier = Modifier.fillMaxSize(),
            energyLevel = energyLevel
        )

        Column(modifier = Modifier.fillMaxSize()) {
            
            // App Bar Overlay
            TopAppBar(
                title = { Text("The Pulse", fontWeight = FontWeight.Bold, color = Color.White) },
                actions = {
                    TextButton(onClick = onNavigateToDashboard) {
                        Text("Command Center", color = Color.Cyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            // Chat Interface
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                reverseLayout = true // Scroll to bottom naturally
            ) {
                // To keep messages flowing top to bottom while `reverseLayout` is on, 
                // we iterate over a reversed list.
                items(messages.reversed()) { msg ->
                    ChatBubble(msg)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Input Dock
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .glassmorphism(RoundedCornerShape(24.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Log your focus, habits, or decisions...", color = Color.White.copy(0.6f)) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 3
                )
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF3B82F6), shape = RoundedCornerShape(16.dp))
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .glassmorphism(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    ),
                    startColor = if (message.isUser) Color(0xFF3B82F6).copy(0.6f) else Color.White.copy(0.1f),
                    endColor = if (message.isUser) Color(0xFF2563EB).copy(0.4f) else Color.White.copy(0.05f)
                )
                .padding(16.dp)
        ) {
            if (message.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else if (message.isUser) {
                Text(message.content, color = Color.White)
            } else {
                TypewriterText(text = message.content, style = TextStyle(color = Color.White))
            }
        }
    }
}
