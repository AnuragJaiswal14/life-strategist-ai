package com.aistrategist.app.domain.usecase

import com.aistrategist.app.domain.model.DailyLog
import com.aistrategist.app.domain.repository.LogRepository
import javax.inject.Inject

class SubmitLogUseCase @Inject constructor(
    private val repository: LogRepository
) {
    suspend operator fun invoke(log: DailyLog) {
        repository.submitLog(log)
    }
}
