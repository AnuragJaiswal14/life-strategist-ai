package com.aistrategist.app.domain.usecase

import com.aistrategist.app.domain.model.DailyLog
import com.aistrategist.app.domain.repository.LogRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetLogsUseCaseTest {

    private val repository: LogRepository = mockk()
    private val getLogsUseCase = GetLogsUseCase(repository)

    @Test
    fun `invoke should return logs from repository`() = runBlocking {
        // Arrange
        val mockLogs = listOf(
            DailyLog("2026-03-22", 8, "{\"work\": 4}", "{\"exercise\": true}"),
            DailyLog("2026-03-21", 6, "{\"work\": 6}", "{\"exercise\": false}")
        )
        every { repository.getAllLogs() } returns flowOf(mockLogs)

        // Act
        val result = getLogsUseCase().toList()

        // Assert
        assertEquals(1, result.size)
        assertEquals(2, result[0].size)
        assertEquals(8, result[0][0].energyLevel)
    }
}
