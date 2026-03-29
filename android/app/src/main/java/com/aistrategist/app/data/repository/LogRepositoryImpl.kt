package com.aistrategist.app.data.repository

import com.aistrategist.app.data.local.dao.LogDao
import com.aistrategist.app.data.local.entity.LogEntity
import com.aistrategist.app.data.remote.ApiService
import com.aistrategist.app.domain.model.DailyLog
import com.aistrategist.app.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.aistrategist.app.data.remote.dto.ChatMessageDto
import com.aistrategist.app.data.remote.dto.ChatRequestDto
import com.aistrategist.app.data.remote.dto.ChatResponseDto
import javax.inject.Inject

class LogRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val logDao: LogDao
) : LogRepository {
    override fun getAllLogs(): Flow<List<DailyLog>> {
        return logDao.getAllLogs().map { entities ->
            entities.map { 
                DailyLog(it.date, it.energyLevel, it.timeSpentJson, it.habitsJson) 
            }
        }
    }

    override suspend fun submitLog(log: DailyLog) {
        val entity = LogEntity(log.date, log.energyLevel, log.timeSpentJson, log.habitsJson)
        logDao.insertLog(entity)
        
        try {
            apiService.submitLog(mapOf(
                "date" to log.date,
                "energyLevel" to log.energyLevel,
                "timeSpent" to log.timeSpentJson, 
                "habits" to log.habitsJson
            ))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun sendChatMessage(messages: List<ChatMessageDto>): Result<ChatResponseDto> {
        return try {
            val response = apiService.sendChatMessage(ChatRequestDto(messages))
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
