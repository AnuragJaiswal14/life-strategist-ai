package com.aistrategist.app.data.remote

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import com.aistrategist.app.data.remote.dto.ChatRequestDto
import com.aistrategist.app.data.remote.dto.ChatResponseDto

interface ApiService {
    @POST("logs")
    suspend fun submitLog(@Body logData: Map<String, @JvmSuppressWildcards Any>): Any

    @GET("reports")
    suspend fun getReports(): List<Any>
    
    @POST("reports/generate")
    suspend fun generateWeeklyReport(): Any

    @POST("logs/chat")
    suspend fun sendChatMessage(@Body request: ChatRequestDto): ChatResponseDto
}
