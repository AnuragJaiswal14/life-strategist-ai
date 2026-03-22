package com.aistrategist.app.domain.model

data class DailyLog(
    val date: String,
    val energyLevel: Int,
    val timeSpentJson: String,
    val habitsJson: String
)
