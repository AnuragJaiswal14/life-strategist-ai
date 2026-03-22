package com.aistrategist.app.domain.repository

import com.aistrategist.app.domain.model.DailyLog
import kotlinx.coroutines.flow.Flow

interface LogRepository {
    fun getAllLogs(): Flow<List<DailyLog>>
    suspend fun submitLog(log: DailyLog)
}
