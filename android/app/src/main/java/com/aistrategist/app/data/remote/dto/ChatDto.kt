package com.aistrategist.app.data.remote.dto

data class ChatMessageDto(
    val role: String,
    val content: String
)

data class ChatRequestDto(
    val messages: List<ChatMessageDto>
)

data class ChatResponseDto(
    val status: String,
    val message: String,
    val log: Any? = null
)
