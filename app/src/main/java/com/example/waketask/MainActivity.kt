package com.example.waketask

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * MainActivity
 * ------------
 * The normal screen you see when you open the app. Lets you:
 *   1. Pick a time using Material 3's built-in TimePicker widget.
 *   2. Choose which kind of verification task you want (Bible verse or Cloud term).
 *   3. Tap "Set Alarm" to schedule everything through AlarmScheduler.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNecessaryPermissions()
        setContent { AppRoot() }
    }

    /** Asks for notification + microphone permission up front, on the Android versions that need it. */
    private fun requestNecessaryPermissions() {
        val permissionsNeeded = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }
        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), 1)
        }
    }

    /** On Android 12+, apps must separately be granted the right to schedule EXACT alarms. */
    private fun ensureExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                startActivity(
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.parse("package:$packageName")
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AppRoot() {
        // TimePickerState holds whatever hour/minute the picker widget currently shows.
        val timeState = rememberTimePickerState(initialHour = 6, initialMinute = 0, is24Hour = false)
        var taskType by remember { mutableStateOf("BIBLE") }
        var confirmation by remember { mutableStateOf<String?>(null) }

        MaterialTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Wake & Task Alarm",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // The built-in Material 3 time picker widget - no custom code needed.
                    TimePicker(state = timeState)

                    Spacer(Modifier.height(24.dp))
                    Text("Verification task type:", fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        FilterChip(
                            selected = taskType == "BIBLE",
                            onClick = { taskType = "BIBLE" },
                            label = { Text("Bible Verse") }
                        )
                        Spacer(Modifier.width(12.dp))
                        FilterChip(
                            selected = taskType == "CLOUD",
                            onClick = { taskType = "CLOUD" },
                            label = { Text("Cloud Term") }
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                    Button(onClick = {
                        ensureExactAlarmPermission()
                        AlarmScheduler.schedule(this@MainActivity, timeState.hour, timeState.minute, taskType)
                        confirmation = "Alarm set for %02d:%02d".format(timeState.hour, timeState.minute)
                    }) {
                        Text("Set Alarm")
                    }

                    confirmation?.let {
                        Spacer(Modifier.height(16.dp))
                        Text(it, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
