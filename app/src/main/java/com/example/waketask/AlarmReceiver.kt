package com.example.waketask

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

/**
 * AlarmReceiver
 * -------------
 * A BroadcastReceiver "listens" for system-wide announcements (called broadcasts).
 * You never create or call this class yourself - Android automatically runs its
 * onReceive() function the instant our scheduled alarm time arrives, even if our
 * app isn't open or the phone is asleep.
 *
 * We keep this class deliberately tiny: Android only gives a BroadcastReceiver a
 * few seconds before it's forcibly shut down, so all we do here is immediately
 * hand off to AlarmForegroundService, which is allowed to keep running much longer.
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Read back which task type was requested when the alarm was set ("BIBLE" default).
        val taskType = intent.getStringExtra("TASK_TYPE") ?: "BIBLE"

        val serviceIntent = Intent(context, AlarmForegroundService::class.java).apply {
            putExtra("TASK_TYPE", taskType)
        }

        // The version-safe way to start a service that's about to show a notification,
        // required on Android 8.0 and above.
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
