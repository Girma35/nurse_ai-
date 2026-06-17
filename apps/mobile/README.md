# Nursy AI Android App

This is the mobile-first product surface for Nursy AI.

## Stack

- Kotlin
- Jetpack Compose
- Room DB
- WorkManager
- Retrofit
- Local rules engine

## Local Setup

1. Open `apps/mobile` in Android Studio.
2. Let Android Studio sync the Gradle project and download Android dependencies.
3. Set the Android SDK path in `local.properties` if Android Studio does not create it automatically.
4. Run the `app` configuration on an emulator or Android device.

This workspace does not include a Gradle wrapper yet. After Android Studio syncs successfully, generate one from the Android Studio Gradle tools or run `gradle wrapper` if Gradle is installed locally.

## Current Scope

The current mobile scaffold includes:

- Compose dashboard starter
- Room entities and DAO for check-ins, symptoms, and medications
- Local rules engine starter
- WorkManager sync worker placeholder

Room remains the local source of truth. Sync should upload queued records to the cloud backend when the device is online.
