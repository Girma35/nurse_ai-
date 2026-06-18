# Nursy AI Android App

This is the primary product surface for Nursy AI.

```text
Mobile-first, offline-first, cloud-enhanced.
```

The app should remain useful without internet. AWS sync and cloud AI are enhancements, not requirements for the daily experience.

## Stack

- Kotlin
- Jetpack Compose
- Room DB
- WorkManager
- OkHttp / Retrofit
- Local rules engine

## Local Setup

1. Open `apps/mobile` in Android Studio.
2. Let Android Studio sync the Gradle project and download Android dependencies.
3. Set the Android SDK path in `local.properties` if Android Studio does not create it automatically.
4. Run the `app` configuration on an emulator or Android device.

## Commands

```bash
./gradlew build
./gradlew test
./gradlew assembleDebug
./gradlew compileDebugKotlin
```

Build output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Current Scope

- Compose app shell and screens
- Room entities and DAO for health data
- Local rules engine for offline insights
- AI symptom journal parser
- Notification/reminder helpers
- WorkManager sync worker
- DynamoDB-compatible sync mapper

Room remains the local source of truth. Sync uploads queued records to the AWS backend when the device is online.
