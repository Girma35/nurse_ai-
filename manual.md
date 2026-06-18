# Nursy AI Manual & Verification Checklist

Offline-first Android health companion with optional AWS sync and premium cloud reports.

## 1. Product Rule

```text
Mobile-first, offline-first, cloud-enhanced.
```

Core app logic runs on the phone. AWS handles auth, backup, sync, and recovery. Cloud AI is used occasionally for weekly/monthly summaries, not daily app behavior.

## 1A. Health Safety Wording Rule

Nursy AI is a health tracking companion. It must not provide diagnosis, disease prediction, treatment instructions, or emergency diagnosis.

Use wording like:

- "This is a pattern worth reviewing with a doctor."
- "You might want to mention this at your next appointment."
- "Here is a summary of your symptoms."
- "No diagnosis is provided, only tracking insights."

Avoid wording like "you may have disease X", "this is likely diabetes/depression/infection", prescribing treatment, replacing doctor advice, or emergency diagnosis claims.

## 2. Prerequisites

| Tool | Version | Check Command |
|---|---|---|
| Java / JDK | 17+ | `java -version` |
| Android SDK | 35+ | `sdkmanager --list` |
| Gradle | wrapper | `cd apps/mobile && ./gradlew --version` |
| ADB | latest | `adb --version` |
| Emulator or physical device | any supported Android device | `adb devices` |

## 3. Project Structure

```text
nurse_ai/
├── apps/mobile/
│   ├── app/src/main/java/com/nursyai/
│   │   ├── ai/                  # LocalRulesEngine
│   │   ├── data/local/          # Room DB, DAO, entities
│   │   ├── navigation/          # Compose routes
│   │   ├── notification/        # Reminders and notification helpers
│   │   ├── sync/                # WorkManager sync + API payload mapping
│   │   └── ui/                  # Compose screens, theme, ViewModel
│   ├── build.gradle.kts
│   └── settings.gradle.kts
├── infra/dynamodb/              # DynamoDB table definition
├── skills/                      # Repo-local mobile implementation skills
├── guide
├── component
└── manual.md
```

## 4. Build Commands

```bash
cd apps/mobile

./gradlew clean
./gradlew compileDebugKotlin
./gradlew test
./gradlew assembleDebug
./gradlew build
```

Debug APK:

```text
apps/mobile/app/build/outputs/apk/debug/app-debug.apk
```

## 5. Install On Device

```bash
adb devices
cd apps/mobile && ./gradlew assembleDebug && cd ../..
adb install -r -d -g apps/mobile/app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.nursyai/.MainActivity
```

Useful commands:

```bash
adb shell pm clear com.nursyai
adb shell am force-stop com.nursyai
adb logcat -v time | grep -i nursy
```

## 6. Verification Checklist

### Foundation

- [ ] Room entities exist for profile, contacts, check-ins, symptoms, medications, and dose events.
- [ ] `HealthDao` supports inserts, history queries, pending sync queries, and mark-synced updates.
- [ ] `NursyDatabase` opens locally and remains the source of truth.

### Mobile Experience

- [ ] App launches into the Compose dashboard.
- [ ] Daily check-in saves locally and updates the dashboard.
- [ ] Symptoms save locally, can be resolved, and appear in history.
- [ ] Medications and dose events save locally.
- [ ] Emergency card is available offline.
- [ ] Timeline reads from local records.

### Local AI

- [ ] `LocalRulesEngine` returns a welcome insight for no data.
- [ ] Low sleep, fatigue, hydration, stress, repeated symptoms, and high severity rules work.
- [ ] Natural language journal parsing extracts known symptoms and durations.
- [ ] Local AI output avoids diagnosis language.
- [ ] Local AI output uses tracking-summary phrasing and never suggests a specific disease or treatment.

### Notifications

- [ ] Notification permission is requested where required.
- [ ] Medication reminders can be scheduled and cancelled.
- [ ] Check-in and hydration reminders use local scheduling.

### Sync

- [ ] Unsynced records are queued locally.
- [ ] `RecordMapper` creates DynamoDB-compatible payloads.
- [ ] `HealthSyncWorker` retries transient failures.
- [ ] Records are marked synced only after confirmed upload.
- [ ] Failed sync leaves local records intact.

### AWS

- [ ] DynamoDB table uses `PAY_PER_REQUEST`.
- [ ] User records are scoped by `PK = USER#<userId>`.
- [ ] API endpoints validate ownership and record type.
- [ ] Cloud AI report generation is optional and cached by date range.

## 7. Development Log

| Date | Version | Notes |
|---|---|---|
| 2026-06-18 | 0.1.0 | Mobile-only direction adopted: offline Android app as core product, AWS for auth/sync/recovery, optional cloud AI reports. Removed web dashboard and shared TypeScript workspace. |

## 8. Troubleshooting

| Problem | Fix |
|---|---|
| Gradle cannot find Android SDK | Create `apps/mobile/local.properties` with `sdk.dir=/path/to/Android/Sdk` |
| Device not listed | Start emulator or enable USB debugging, then run `adb devices` |
| Notification reminders do not show | Check notification permission and device battery restrictions |
| Sync retries forever | Inspect backend response, `SyncApiClient` base URL, and WorkManager logs |
