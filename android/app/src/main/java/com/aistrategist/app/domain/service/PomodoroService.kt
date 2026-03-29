package com.aistrategist.app.domain.service

import android.app.*
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PomodoroService : Service() {

    private var timer: CountDownTimer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val durationMins = intent.getIntExtra(EXTRA_DURATION_MINS, 25)
                startTimer(durationMins)
            }
            ACTION_STOP -> {
                stopTimer()
            }
        }
        return START_NOT_STICKY
    }

    private fun startTimer(durationMins: Int) {
        val durationMillis = durationMins * 60 * 1000L
        
        createNotificationChannel()
        val notification = createNotification("Focus session started: $durationMins min")
        startForeground(NOTIFICATION_ID, notification)

        _isRunning.value = true
        _timeLeftInMillis.value = durationMillis

        timer?.cancel()
        timer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeftInMillis.value = millisUntilFinished
            }

            override fun onFinish() {
                _isRunning.value = false
                _timeLeftInMillis.value = 0
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }.start()
    }

    private fun stopTimer() {
        timer?.cancel()
        _isRunning.value = false
        _timeLeftInMillis.value = 0
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pomodoro Timer",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Pomodoro Active")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_DURATION_MINS = "EXTRA_DURATION_MINS"
        private const val CHANNEL_ID = "pomodoro_channel"
        private const val NOTIFICATION_ID = 1

        private val _isRunning = MutableStateFlow(false)
        val isRunning = _isRunning.asStateFlow()

        private val _timeLeftInMillis = MutableStateFlow(0L)
        val timeLeftInMillis = _timeLeftInMillis.asStateFlow()

        fun calculateOptimalInterval(energyLevel: Int): Int {
            return when {
                energyLevel >= 8 -> 50 // High energy: Deep work (50 min)
                energyLevel >= 5 -> 25 // Medium energy: Standard Pomodoro (25 min)
                else -> 15 // Low energy: Short bursts (15 min)
            }
        }
    }
}
