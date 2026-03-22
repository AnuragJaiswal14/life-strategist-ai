package com.aistrategist.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_logs")
data class LogEntity(
    @PrimaryKey
    val date: String,
    val energyLevel: Int,
    val timeSpentJson: String,
    val habitsJson: String
)
