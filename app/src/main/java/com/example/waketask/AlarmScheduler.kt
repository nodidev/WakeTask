package com.example.waketask

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

/**
 * AlarmScheduler
 * ---------------
 * This is a Kotlin "object" - meaning it's a singleton (only one copy ever exists,
 * so you never write "AlarmScheduler()" - you just call AlarmScheduler.schedule(...)
 * directly, like calling a function on a toolbox).
 *
 * Its only job is to talk to Android's built-in AlarmManager system service and say
 * "please wake my app up at this exact time, even if the phone is asleep."
 */
object AlarmScheduler {

    // A unique ID we attach to our alarm's PendingIntent, so Android can find and
    // cancel this exact alarm later if we ask it to.
    private const val REQUEST_CODE = 1001

    /**
     * Schedules the alarm.
     *
     * @param context   Needed to reach Android's system services.
     * @param hour      24-hour format hour (e.g. 6 for 6 AM).
     * @param minute    Minute of the hour (0-59).
     * @param taskType  Which verification task to show when it fires ("BIBLE" or "CLOUD").
     */
    fun schedule(context: Context, hour: Int, minute: Int, taskType: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // This Intent says "when the alarm fires, deliver it to AlarmReceiver",
        // and attaches which task type the user picked.
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TASK_TYPE", taskType)
        }

        // A PendingIntent is like a signed permission slip: it lets AlarmManager (a
        // different part of Android, running outside our app) trigger our Intent later,
        // on our behalf, even after our app process has been closed.
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Work out the exact date/time (today, or tomorrow if that time already passed).
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        // setAlarmClock() is the MOST reliable way to schedule a wake-up alarm on
        // Android. Unlike a plain setExact(), the OS treats it like a real alarm-clock
        // app's alarm, which makes it far less likely to be delayed by battery saving.
        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            calendar.timeInMillis,
            pendingIntent // shown if the user taps the little alarm icon in their status bar
        )
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    /** Cancels any alarm previously scheduled with schedule(). */
    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
