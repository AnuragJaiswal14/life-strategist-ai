package com.aistrategist.app.data.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import com.aistrategist.app.domain.model.AppUsageData

@Singleton
class AppUsageRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun getTopAppUsages(daysBack: Int = 7): List<AppUsageData> = withContext(Dispatchers.IO) {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val pm = context.packageManager
        
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysBack)
        val startTime = cal.timeInMillis
        val endTime = System.currentTimeMillis()
        
        val usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        
        if (usageStatsList.isNullOrEmpty()) return@withContext emptyList()
        
        val appUsageMap = mutableMapOf<String, Long>()
        
        for (usageStats in usageStatsList) {
            val packageName = usageStats.packageName
            val totalTimeInForeground = usageStats.totalTimeInForeground
            
            if (totalTimeInForeground > 0) {
                // Heuristic Engine: Only collect foreground time of apps the user explicitly launched
                if (pm.getLaunchIntentForPackage(packageName) != null) {
                    val currentTracked = appUsageMap[packageName] ?: 0L
                    appUsageMap[packageName] = currentTracked + totalTimeInForeground
                }
            }
        }
        
        // Sort, crop to top 5 (for perfect Vico mapping & Bento Grid fitting), and map to labels
        val sortedList = appUsageMap.entries.sortedByDescending { it.value }.take(5)
        
        return@withContext sortedList.map { entry ->
            val pkg = entry.key
            val ms = entry.value
            val label = try {
                pm.getApplicationInfo(pkg, 0).loadLabel(pm).toString()
            } catch (e: Exception) {
                // Fallback constraint if OS metadata lookup drops
                val truncated = pkg.split(".").lastOrNull() ?: pkg
                truncated.replaceFirstChar { it.uppercase() }
            }
            val hours = ms / (1000f * 60f * 60f)
            AppUsageData(label, hours)
        }
    }

    suspend fun get24HourEnergyROI(): FloatArray = withContext(Dispatchers.IO) {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val roiMap = FloatArray(24) { 0f }
        
        val currentTime = System.currentTimeMillis()
        val oneHourMs = 1000L * 60 * 60
        
        // Stateful Physiological Battery: Start at an assumed generic 60% 24-hours ago
        var currentEnergy = 60f 
        
        // Loop chronologically from left (24 hours ago) to right (current hour)
        for (i in 0 until 24) {
            val startTime = currentTime - (oneHourMs * (24 - i))
            val endTime = startTime + oneHourMs
            
            // Critical Matrix Change: queryUsageStats aggregates into generic Daily buckets causing identical output 'ceilings'.
            // Using queryEvents targets sub-daily micro-interactions precisely matching Android battery tracker specs!
            val events = usageStatsManager.queryEvents(startTime, endTime)
            val event = android.app.usage.UsageEvents.Event()
            
            var lastResumeTime = 0L
            var totalForegroundMs = 0L
            
            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                
                if (event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED) {
                    lastResumeTime = event.timeStamp
                } else if (event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_PAUSED || 
                           event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_STOPPED) {
                    if (lastResumeTime > 0 && event.timeStamp > lastResumeTime) {
                        totalForegroundMs += (event.timeStamp - lastResumeTime)
                        lastResumeTime = 0L // Reset
                    }
                }
            }
            
            // Handle edge-case if hour bucket ends while user is actively holding phone
            if (lastResumeTime > 0 && endTime > lastResumeTime) {
                totalForegroundMs += (endTime - lastResumeTime)
            }
            
            val foregroundMinutes = (totalForegroundMs / (1000f * 60f))
            
            // The Realistic Mathematical Energy Model:
            if (foregroundMinutes < 5f) {
                // Device untouched (Deep Focus, Napping, Sleeping). Charge the human battery +15%.
                currentEnergy += 15f 
            } else {
                // Device active. Usage rapidly drains cognitive energy capacity.
                // 40 mins doom-scroll = -20% Energy drain multiplier.
                val drain = foregroundMinutes * 0.5f 
                currentEnergy -= drain
            }
            
            // Clamp exhaustion threshold to 10% and peak capacity to 100%
            currentEnergy = currentEnergy.coerceIn(10f, 100f)
            
            roiMap[i] = currentEnergy
        }
        
        return@withContext roiMap
    }
}
