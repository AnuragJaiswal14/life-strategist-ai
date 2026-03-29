package com.aistrategist.app.domain.repository

import com.aistrategist.app.domain.model.DailyLog
import kotlinx.coroutines.flow.Flow
import com.aistrategist.app.data.remote.dto.ChatMessageDto
import com.aistrategist.app.data.remote.dto.ChatResponseDto

interface LogRepository {
    fun getAllLogs(): Flow<List<DailyLog>>
    suspend fun submitLog(log: DailyLog)
    suspend fun sendChatMessage(messages: List<ChatMessageDto>): Result<ChatResponseDto>
}
