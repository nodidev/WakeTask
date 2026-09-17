// This file configures the ":app" module specifically - the actual app we ship.
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.waketask"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.waketask"
        // minSdk 26 = Android 8.0 - the earliest version with notification channels
        // and foreground-service rules we rely on for reliable alarm ringing.
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    // Turns on Jetpack Compose, the modern toolkit we use to build the UI screens.
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")

    // The Compose "Bill of Materials" - keeps all Compose library versions in sync.
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Needed so the AndroidManifest.xml theme (Theme.Material3...) resolves correctly.
    implementation("com.google.android.material:material:1.12.0")
}
