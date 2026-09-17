# Wake Task Alarm — Complete Beginner's Setup Guide

You've never used Android Studio before — this guide assumes that, and walks
through literally every click. Follow it top to bottom in order.

---

## Part 0 — What you're about to do (big picture)

1. Install Android Studio (one-time, ~30-60 min depending on your internet).
2. Open the `WakeTask` project folder I gave you inside it.
3. Wait for it to download some extra files automatically ("Gradle sync").
4. Plug in your Android phone and hit the green ▶ Run button.
5. The app installs on YOUR phone and you test it for real.

You will not need to touch the command line at all — everything below is
clicking buttons in the Android Studio window.

---

## Part 1 — Install Android Studio

1. Go to **https://developer.android.com/studio** in your browser.
2. Click the big **Download Android Studio** button. Accept the terms.
3. Run the installer you downloaded:
   - **Windows**: double-click the `.exe`, click Next through the wizard,
     leave all default options checked, click Finish.
   - **Mac**: open the `.dmg`, drag the Android Studio icon into
     Applications, then open it from Applications (first time, macOS may
     ask you to confirm you trust it — click Open).
4. The first time Android Studio opens, a **Setup Wizard** appears:
   - Choose **"Standard"** install type when asked.
   - Keep clicking **Next** through the screens (UI theme, license
     agreements — click **Accept** for each license, then **Finish**).
   - It will now download the Android SDK and emulator files. This is the
     big download (several GB) — let it finish. You'll land on a
     **"Welcome to Android Studio"** window when it's done.

---

## Part 2 — Open the WakeTask project

1. Unzip the `WakeTask.zip` file I gave you somewhere easy to find, like
   your Desktop. You should end up with a folder literally named `WakeTask`
   containing files like `build.gradle.kts`, `settings.gradle.kts`, and an
   `app` folder.
2. In the **Welcome to Android Studio** window, click **Open** (not "New
   Project" — you already have one).
3. Browse to and select the `WakeTask` folder itself (the one containing
   `settings.gradle.kts`), then click **Open**.
4. Android Studio will open the project. At the bottom of the window you'll
   see a progress bar labeled **"Gradle sync"** or similar — this is Android
   Studio downloading all the libraries the project needs (Compose,
   Material 3, etc.). **Let this finish** — it can take 5-15 minutes the
   first time. Don't close the window.
5. If a yellow/blue banner appears at the top saying something like
   *"Android Gradle Plugin update recommended"* — click whatever button
   accepts it (e.g. "Update"). This is normal and safe to accept.
6. If sync finishes with **red errors** instead of finishing cleanly, copy
   the red text and send it to me — don't worry, this is a normal part of
   setting up any real project and easy to fix together.

---

## Part 3 — Add the alarm sound file

The code expects a sound file that isn't included (to keep the download
small and avoid copyright issues with any specific ringtone).

1. Find any short `.mp3` file you like on your computer (even a 5-second
   clip works for testing).
2. On the **left side** of Android Studio, there's a panel usually labeled
   **"Project"**. Make sure its dropdown at the top says **"Android"** (not
   "Project Files").
3. Navigate: `app` → `res` → right-click on the **`raw`** folder — this
   folder already exists but is empty, so use your computer's normal file
   explorer (Finder/File Explorer) to drag-and-drop your `.mp3` file
   directly into: `WakeTask/app/src/main/res/raw/`
4. **Rename it exactly to** `alarm_sound.mp3` (lowercase, that exact name —
   the code specifically looks for this filename).
5. Back in Android Studio, right-click the `app` folder in the Project
   panel and choose **"Synchronize"** (or just wait a moment — it usually
   notices the new file automatically).

---

## Part 4 — Connect your Android phone

You need a real phone for this (lock-screen + microphone behavior doesn't
feel real in the emulator).

1. On your phone: **Settings → About phone** → find **"Build number"** →
   tap it **7 times in a row**. You'll see a message "You are now a
   developer!"
2. Go back to **Settings**, you'll now see a new menu called
   **"Developer options"**. Open it.
3. Turn on **"USB debugging"**.
4. Plug your phone into your computer with a USB cable.
5. Your phone will pop up a dialog: **"Allow USB debugging?"** — tap
   **Allow** (check "always allow from this computer" if you want).
6. In Android Studio, at the top toolbar, there's a **device dropdown**
   (usually says "No devices" or shows an emulator name). Click it — your
   phone's model name should now appear in the list. Select it.

If your phone doesn't appear: try a different USB cable (some cables are
charge-only), or re-check Developer Options → USB debugging is on.

---

## Part 5 — Run the app

1. With your phone selected in the device dropdown, click the green
   **▶ (Run 'app')** button in the top toolbar (or press the keyboard
   shortcut shown next to it).
2. Android Studio will build the app (progress bar at the bottom) and then
   automatically install and open it on your phone. First build can take a
   couple of minutes.
3. On your phone, you'll get permission pop-ups — tap **Allow** for
   notifications and microphone access.

---

## Part 6 — Test it

1. In the app on your phone, pick a time **1-2 minutes from now** using the
   time picker, choose "Bible Verse" or "Cloud Term", tap **Set Alarm**.
2. If Android asks you to allow "Alarms & reminders" — tap through to the
   settings screen it opens and enable it for this app.
3. Lock your phone (press the power button) and wait.
4. At the set time, the screen should turn on by itself, show the ringing
   full-screen task page, and play your sound on loop.
5. Tap **🎤 Start Reading**, read the on-screen text out loud when prompted,
   and it should detect a match and stop the alarm.

---

## Where to look if something goes wrong

- **Logcat**: the tab usually at the bottom of Android Studio labeled
  "Logcat" — this shows live error messages from your phone while the app
  runs. If the app crashes, red text will appear here. Copy/paste any red
  text to me and I'll help debug it.
- **Build → Clean Project**, then **Build → Rebuild Project** (top menu) —
  a good first fix to try if something seems stuck or won't update.
- If Gradle sync fails: click the **"Sync Project with Gradle Files"**
  button (looks like an elephant/circular-arrow icon in the toolbar) to
  retry it.

---

## Quick glossary (Android Studio terms you'll see)

| Term | What it means |
|---|---|
| **Gradle** | The build system — turns your code into an installable app |
| **Gradle sync** | Downloading/checking dependencies; happens after opening or editing build files |
| **Logcat** | Live log/console output from your running app — your main debugging tool |
| **Run configuration** | What "app" you're running (the dropdown next to the ▶ button) |
| **APK** | The final installable Android app file (like a `.exe` for Windows) |
| **SDK** | Software Development Kit — the Android platform files/tools Studio needs |

---

## What's next

Once it's running on your phone, tell me what happened (worked fine? a
specific error? Logcat text?) and we'll move to Phase 2: making the alarm
survive a phone restart, adding a custom font, and any of the extra
features you want. Contact me at odoemechinonso@gmail.com
