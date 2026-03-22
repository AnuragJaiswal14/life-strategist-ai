package com.aistrategist.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aistrategist.app.data.local.dao.LogDao
import com.aistrategist.app.data.local.entity.LogEntity

@Database(entities = [LogEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
}
