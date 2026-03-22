package com.aistrategist.app.domain.usecase

import com.aistrategist.app.domain.model.DailyLog
import com.aistrategist.app.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLogsUseCase @Inject constructor(
    private val repository: LogRepository
) {
    operator fun invoke(): Flow<List<DailyLog>> {
        return repository.getAllLogs()
    }
}
