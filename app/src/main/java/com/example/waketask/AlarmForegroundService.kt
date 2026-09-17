package com.example.waketask

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

/**
 * AlarmForegroundService
 * -----------------------
 * A "foreground service" is a background task Android promises NOT to kill, as long
 * as it keeps a visible notification on screen the whole time it's running. We need
 * this because ringing the alarm and waiting for the user to complete their task can
 * take a while - much longer than a BroadcastReceiver is ever allowed to run.
 *
 * When this service starts, it does two things:
 *   1. Plays the alarm sound on a loop.
 *   2. Posts a special "full-screen intent" notification - the one kind of notification
 *      Android allows to burst open over the LOCK SCREEN itself, automatically launching
 *      AlarmActivity, exactly the way a real alarm-clock app behaves.
 */
class AlarmForegroundService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    companion object {
        const val CHANNEL_ID = "alarm_channel"
        const val NOTIFICATION_ID = 42
    }

    // We don't need two-way "binding" for this service - other components just
    // start/stop it, so this can safely return null.
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val taskType = intent?.getStringExtra("TASK_TYPE") ?: "BIBLE"

        createNotificationChannel()
        // startForeground() must be called within a few seconds of the service starting,
        // or Android will kill it - this line is what "promotes" us to a protected state.
        startForeground(NOTIFICATION_ID, buildFullScreenNotification(taskType))
        startRinging()

        // START_STICKY tells Android: "if you ever have to kill this service for memory,
        // please restart it as soon as possible" - we want the alarm to keep trying.
        return START_STICKY
    }

    /** Starts looping the alarm sound. */
    private fun startRinging() {
        // NOTE: you must add your own sound file at
        // app/src/main/res/raw/alarm_sound.mp3  (any short mp3/ogg/wav works).
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)?.apply {
            isLooping = true
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM) // tells Android "this IS an alarm sound"
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            start()
        }
    }

    /** Stops the sound and shuts the service down. Called by AlarmActivity once the task is passed. */
    fun stopRinging() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /** Android 8+ requires every notification to belong to a "channel" registered ahead of time. */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm",
                NotificationManager.IMPORTANCE_HIGH // high importance = allowed to full-screen
            )
            channel.description = "Wake-up alarm notifications"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Builds the special notification Android will show even over a locked screen,
     * automatically opening AlarmActivity (our ringing + verification screen).
     */
    private fun buildFullScreenNotification(taskType: String): Notification {
        val fullScreenIntent = Intent(this, AlarmActivity::class.java).apply {
            putExtra("TASK_TYPE", taskType)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Wake up!")
            .setContentText("Complete your task to stop the alarm")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true) // <- this is the key line
            .setOngoing(true) // the user can't just swipe the notification away
            .build()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}
