package com.aistrategist.app.domain.usecase

import android.content.Context
import android.provider.CalendarContract
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

data class CalendarEvent(
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val isAllDay: Boolean
)

class CalendarSyncUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun getUpcomingEvents(daysAhead: Int = 7): List<CalendarEvent> = withContext(Dispatchers.IO) {
        val events = mutableListOf<CalendarEvent>()
        
        val projection = arrayOf(
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.ALL_DAY
        )
        
        val now = Calendar.getInstance().timeInMillis
        val future = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, daysAhead) }.timeInMillis
        
        val selection = "${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} <= ?"
        val selectionArgs = arrayOf(now.toString(), future.toString())
        
        try {
            val cursor = context.contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                "${CalendarContract.Events.DTSTART} ASC"
            )
            
            cursor?.use {
                val titleIndex = it.getColumnIndexOrThrow(CalendarContract.Events.TITLE)
                val startIndex = it.getColumnIndexOrThrow(CalendarContract.Events.DTSTART)
                val endIndex = it.getColumnIndexOrThrow(CalendarContract.Events.DTEND)
                val allDayIndex = it.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY)
                
                while (it.moveToNext()) {
                    events.add(
                        CalendarEvent(
                            title = it.getString(titleIndex) ?: "Untitled",
                            startTime = it.getLong(startIndex),
                            endTime = it.getLong(endIndex),
                            isAllDay = it.getInt(allDayIndex) == 1
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        events
    }
}
