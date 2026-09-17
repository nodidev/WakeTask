package com.example.waketask

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * AlarmActivity
 * -------------
 * This is the full-screen "the alarm is ringing" experience - the whole point of the
 * app. It:
 *   1. Forces itself to display even over the lock screen (see the window flags below).
 *   2. Shows a randomly chosen task (Bible verse or cloud term) from TaskProvider.
 *   3. Lets the user tap a microphone button, listens to what they say using Android's
 *      built-in speech-to-text screen, and checks how closely it matches.
 *   4. Once they pass, tells AlarmForegroundService to stop ringing, then closes.
 *
 * Until step 4 happens, there is deliberately NO other way to leave this screen -
 * that's what makes the alarm actually work as a "you must complete the task" alarm.
 */
class AlarmActivity : ComponentActivity() {

    private lateinit var task: VerificationTask

    // The modern, safe way to launch another screen (here: Android's built-in
    // "listen and transcribe speech" UI) and receive its result back, without
    // manually juggling request codes like older Android code used to.
    private val speechLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spokenText = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull() ?: ""
        handleSpokenResult(spokenText)
    }

    // `mutableStateOf` is Compose's way of saying "this is a piece of data the UI
    // should automatically redraw itself around whenever it changes."
    private var feedbackMessage by mutableStateOf("Tap the microphone and read the text aloud.")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // These are the flags that let this one screen appear ON TOP of the lock
        // screen, turn the display on, and keep it on for as long as the alarm rings.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        // Ask for the microphone permission immediately if we don't already have it.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }

        // Figure out which task to show, based on what was chosen back in MainActivity.
        val taskType = intent.getStringExtra("TASK_TYPE") ?: "BIBLE"
        task = TaskProvider.randomTask(taskType)

        setContent { AlarmScreen() }
    }

    /** Launches Android's built-in speech-recognition screen so the user can read the passage. */
    private fun startListening() {
        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Read the text aloud")
        }
        speechLauncher.launch(speechIntent)
    }

    /** Compares what Android heard against this task's expected keywords. */
    private fun handleSpokenResult(spokenText: String) {
        val score = task.matchScore(spokenText)
        if (score >= 0.7) { // at least 70% of the key words were detected - good enough
            feedbackMessage = "Well done! Alarm stopping..."
            // Tell the foreground service to shut down (this also stops the sound),
            // then close this screen so the user is returned to wherever they were.
            stopService(Intent(this, AlarmForegroundService::class.java))
            finish()
        } else {
            feedbackMessage = "Didn't quite catch that (${(score * 100).toInt()}% match). Try again."
        }
    }

    @Composable
    private fun AlarmScreen() {
        MaterialTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("⏰ Wake Up!", fontSize = 34.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(28.dp))
                    Text(task.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        task.promptText,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(Modifier.height(36.dp))
                    Button(onClick = { startListening() }) {
                        Text("🎤  Start Reading", fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(feedbackMessage, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
