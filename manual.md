# Nursy AI — Manual & Verification Checklist

> Offline-first health tracking app with Android (Jetpack Compose + Room) mobile app and Next.js web dashboard.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Project Structure Overview](#2-project-structure-overview)
3. [Build Commands](#3-build-commands)
4. [Install on ADB Devices](#4-install-on-adb-devices)
5. [Run Web Preview](#5-run-web-preview)
6. [Verification Checklist](#6-verification-checklist)
   - [Level 1: Foundation — Data Model & Database](#level-1-foundation--data-model--database)
   - [Level 2: Mobile App — Navigation & Screens](#level-2-mobile-app--navigation--screens)
   - [Level 3: Intelligence — Rules Engine & Journal](#level-3-intelligence--rules-engine--journal)
   - [Level 4: Proactive Behavior — Notifications](#level-4-proactive-behavior--notifications)
   - [Level 5: Cloud — Sync Engine](#level-5-cloud--sync-engine)
   - [Level 6: Web Dashboard — Visualization](#level-6-web-dashboard--visualization)
   - [Level 7: Build & Deploy Verification](#level-7-build--deploy-verification)
7. [Development Log](#7-development-log)

---

## 1. Prerequisites

### For Android (Mobile App)

| Tool | Version | Check Command |
|---|---|---|
| Java / JDK | 17+ | `java -version` |
| Android SDK | 35+ | `sdkmanager --list \| grep "platforms;android-35"` |
| Gradle | 8.9 (wrapper) | `cd apps/mobile && ./gradlew --version` |
| Kotlin | 2.0.21 (compiler plugin) | (included in Gradle) |
| ADB (Android Debug Bridge) | Latest | `adb --version` |
| Android device emulator OR physical device | — | `adb devices` |

### For Web (Dashboard)

| Tool | Version | Check Command |
|---|---|---|
| Node.js | 18+ | `node --version` |
| npm | 10+ | `npm --version` |

### First-Time Setup

```bash
# 1. Install root npm dependencies (shared + web)
npm install

# 2. Verify shared package typecheck
npm run typecheck --workspace @nursy/shared

# 3. Verify web typecheck
npm run typecheck:web

# 4. Set up Android SDK (if not already)
#    Set ANDROID_HOME environment variable
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin
```

---

## 2. Project Structure Overview

```
nurse_ai/
├── apps/
│   ├── mobile/                          # Android app (Kotlin + Compose)
│   │   ├── app/
│   │   │   ├── src/main/java/com/nursyai/
│   │   │   │   ├── ai/                  # LocalRulesEngine
│   │   │   │   ├── data/local/
│   │   │   │   │   ├── dao/             # HealthDao (50+ methods)
│   │   │   │   │   ├── entity/          # 6 Room entities
│   │   │   │   │   └── NursyDatabase.kt
│   │   │   │   ├── navigation/          # NavigationRoutes
│   │   │   │   ├── notification/        # Notifications + Reminders
│   │   │   │   ├── sync/               # HealthSyncWorker, SyncApiClient, RecordMapper
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/         # 8 Compose screens
│   │   │   │   │   ├── theme/           # NursyColors
│   │   │   │   │   └── NursyViewModel.kt
│   │   │   │   └── MainActivity.kt
│   │   │   ├── build.gradle.kts
│   │   │   └── src/main/AndroidManifest.xml
│   │   ├── build.gradle.kts
│   │   └── settings.gradle.kts
│   └── web/                             # Next.js dashboard
│       └── src/
│           ├── app/                     # page.tsx, layout.tsx
│           ├── components/              # 7 reusable components
│           └── lib/                     # api.ts, mock-data, cloud-ai-reports
├── packages/
│   └── shared/                          # @nursy/shared (17 types, 6 helpers)
│       └── src/index.ts
├── infra/
│   └── dynamodb/                        # CloudFormation template
├── skills/                              # 24 skill files (16 component + 8 phase)
│   ├── components/
│   └── phases/
├── manual.md                            ← This file
└── package.json                         # Root workspace
```

---

## 3. Build Commands

### 3.1 Android App — Debug APK

```bash
# From project root
cd apps/mobile

# Clean build (recommended after schema changes)
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build and run tests
./gradlew test

# Build specific variant
./gradlew assembleDebug --stacktrace

# Check for Kotlin + Compile errors only (faster)
./gradlew compileDebugKotlin

# Return to root
cd ../..
```

**APK output location:** `apps/mobile/app/build/outputs/apk/debug/app-debug.apk`

### 3.2 Android App — Release APK

```bash
cd apps/mobile
./gradlew assembleRelease
cd ../..
```

### 3.3 Android Tests

```bash
cd apps/mobile

# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.nursyai.ai.LocalRulesEngineTest"

# Run test with detailed output
./gradlew test --info

cd ../..
```

### 3.4 Web App — TypeCheck & Build

```bash
# From project root

# Type-check shared package
npm run typecheck --workspace @nursy/shared

# Type-check web dashboard
npm run typecheck:web

# Production build (for Vercel deployment)
npm run build:web

# Lint check
npm run lint:web
```

---

## 4. Install on ADB Devices

### 4.1 Prerequisites

```bash
# List connected devices
adb devices

# Expected output:
# List of devices attached
# emulator-5554   device    ← emulator
# R58MA123456     device    ← physical device via USB
```

If no device is shown:
1. **Emulator**: Open Android Studio → Device Manager → Create/Start emulator
2. **Physical device**: Enable Developer Options → USB Debugging → Connect via USB

### 4.2 Install APK

```bash
# 1. Build the debug APK first
cd apps/mobile && ./gradlew assembleDebug && cd ../..

# 2. Install on connected device
adb install -r apps/mobile/app/build/outputs/apk/debug/app-debug.apk

#   -r = replace existing app (reinstall)
#   -d = allow downgrade (if version code decreased)
#   -g = grant all runtime permissions

# Install with all flags:
adb install -r -d -g apps/mobile/app/build/outputs/apk/debug/app-debug.apk
```

### 4.3 Run App After Install

```bash
# Launch the main activity directly
adb shell am start -n com.nursyai/.MainActivity

# Or use the app launcher on the device
```

### 4.4 View Logs

```bash
# Stream all logs
adb logcat -v time

# Filter by app package
adb logcat -v time | grep -i nursery

# Filter by log level (Error/Warning)
adb logcat -v time *:E

# Clear log buffer
adb logcat -c

# Save logs to file
adb logcat -d > app_logs.txt
```

### 4.5 Useful ADB Commands

```bash
# List installed packages
adb shell pm list packages | grep nursy

# Clear app data
adb shell pm clear com.nursyai

# Force stop app
adb shell am force-stop com.nursyai

# Grant notification permission (Android 13+)
adb shell appops set com.nursyai POST_NOTIFICATIONS allow

# Check if exact alarm permission is granted
adb shell dumpsys deviceidle | grep com.nursyai

# Pull database from device for inspection
adb exec-out run-as com.nursyai cat databases/nursy_ai_database > local_backup.db

# Take screenshot
adb shell screencap /sdcard/screenshot.png
adb pull /sdcard/screenshot.png
```

---

## 5. Run Web Preview

### 5.1 Development Server

```bash
# From project root
npm run dev:web

# Starts Next.js at: http://localhost:3000

# To run on a specific port:
cd apps/web && npx next dev -p 4000

# With tunnel (for mobile testing/external access):
npx localtunnel --port 3000
```

### 5.2 Production Preview

```bash
# Build production bundle
npm run build:web

# Start production server
cd apps/web && npx next start
```

### 5.3 Web Dashboard Features to Verify

| Feature | URL | What to Check |
|---|---|---|
| Main dashboard | `http://localhost:3000` | Score ring, metrics, insights, symptoms, timeline, sync, emergency card |
| Demo/Live toggle | Header button | Switches between mock data and live API |
| Loading state | First load or slow network | DashboardSkeleton renders |
| Empty state | Clear data | EmptyState components render |
| Error state | Stop API / offline | ErrorState with retry button |
| Responsive | Resize browser | Mobile → tablet → desktop breakpoints |

---

## 6. Verification Checklist

### Level 1: Foundation — Data Model & Database

- [ ] **6 Room entities exist** in `apps/mobile/.../entity/`
  - [ ] `DailyCheckInEntity` — mood, energyLevel, sleepHours, sleepQuality, stressLevel, waterIntakeMl, notes, date
  - [ ] `SymptomEntity` — name, severity (1-5), startedAt, durationHours, notes, active (Boolean)
  - [ ] `MedicationEntity` — name, dose, frequency, scheduledTimesCsv, takenCount, missedCount, active
  - [ ] `MedicationDoseEventEntity` — medicationId, scheduledTime, takenAt, status ("taken"|"missed"|"skipped")
  - [ ] `ProfileEntity` — fullName, dateOfBirth, gender, weightKg, heightCm, bloodType, allergies, chronicConditions
  - [ ] `EmergencyContactEntity` — name, relationship, phoneNumber
- [ ] **Every entity has sync fields**: id, userId, createdAt, updatedAt, syncState
- [ ] **`NursyDatabase.kt`** — version 2, singleton pattern (`getInstance()`), lists all 6 entities
- [ ] **`HealthDao.kt`** — 50+ methods covering:
  - [ ] Observe (Flow) for all 6 entities
  - [ ] Get (suspend) for all 6 entities
  - [ ] Upsert for all 6 entities
  - [ ] Pending sync queries for all 6 entities
  - [ ] Mark synced queries for all 6 entities
  - [ ] Aggregated pending count queries
- [ ] **`packages/shared/src/index.ts`** — 17 types, 6 helper functions
- [ ] **Command:** `npm run typecheck --workspace @nursy/shared` ✅ passes
- [ ] **Command:** `npm run typecheck:web` ✅ passes
- [ ] **DynamoDB table** `infra/dynamodb/nursy-ai-table.json` — PK `USER#<userId>`, SK `<entity>#<timestamp>#<id>`, GSI1

### Level 2: Mobile App — Navigation & Screens

- [ ] **Navigation structure** (`MainActivity.kt` + `NavigationRoutes.kt`):
  - [ ] 8 routes: dashboard, checkin, symptoms, medications, timeline, profile, emergency, symptom_journal
  - [ ] Bottom nav bar with 4 tabs: Dashboard, Check-In, Symptoms, Medications
  - [ ] `NursyViewModel` shared across all screens via `viewModel()`
- [ ] **`NursyViewModel.kt`**:
  - [ ] StateFlows: `latestCheckIn`, `activeSymptoms`, `activeMedications`, `profile`, `insights`, `pendingSyncCounts`
  - [ ] Flows: `checkIns`, `allSymptoms`, `allMedications`, `doseEvents`, `emergencyContacts`
  - [ ] Actions: `saveCheckIn()`, `addSymptom()`, `resolveSymptom()`, `addMedication()`, `deactivateMedication()`, `markDoseAsTaken()`, `saveProfile()`, `addEmergencyContact()`, `parseJournalNote()`, `saveParsedSymptoms()`, `scheduleDoseEventsForMedication()`
  - [ ] `insights` StateFlow combines `latestCheckIn` + `activeSymptoms` via `LocalRulesEngine`
- [ ] **`DashboardScreen.kt`**:
  - [ ] Health score ring (Canvas arc) with score number
  - [ ] Quick metric row: Sleep hours, Water ml, Energy level
  - [ ] Insight cards from LocalRulesEngine
  - [ ] Active symptom summary cards
  - [ ] Active medication summary cards
  - [ ] Empty state when no data
- [ ] **`DailyCheckInScreen.kt`**:
  - [ ] Mood dropdown: low, down, steady, good, great
  - [ ] Energy level slider (1-10)
  - [ ] Sleep hours text input (decimal)
  - [ ] Sleep quality dropdown: poor, fair, good, excellent
  - [ ] Stress level slider (1-10)
  - [ ] Water intake text input (ml)
  - [ ] Notes text area
  - [ ] Pre-fills from today's existing check-in
  - [ ] Save button → upserts to Room
- [ ] **`SymptomTrackingScreen.kt`**:
  - [ ] Add symptom form: name, severity slider (1-5), duration, notes
  - [ ] Symptom history list with severity and duration
  - [ ] Resolve button on active symptoms
  - [ ] Empty state when no symptoms
- [ ] **`MedicationManagementScreen.kt`**:
  - [ ] Add medication form: name, dose, frequency, schedule (comma-separated times)
  - [ ] Medication list with taken/missed counts
  - [ ] Schedule Doses button
  - [ ] Deactivate button for active medications
  - [ ] Empty state when no medications
- [ ] **`HealthTimelineScreen.kt`**:
  - [ ] Chronological events from check-ins, symptoms, dose events
  - [ ] Date grouping with "MMM dd, yyyy" headers
  - [ ] Colored dot indicators by type (mint=checkin, amber=symptom, moss=dose)
  - [ ] Empty state when no events
- [ ] **`ProfileScreen.kt`**:
  - [ ] Full name, DOB, gender, weight, height inputs
  - [ ] Blood type, allergies, chronic conditions inputs
  - [ ] Save button
- [ ] **`EmergencyCardScreen.kt`**:
  - [ ] Emergency card with blood type, allergies, conditions (coral theme)
  - [ ] Emergency contacts list with name, relationship, phone
  - [ ] Add emergency contact form
  - [ ] Empty state when no profile/contacts
- [ ] **`Theme.kt`**: `NursyColors` (ink, moss, mint, coral, amber, cloud, background, surface)

### Level 3: Intelligence — Rules Engine & Journal

- [ ] **`LocalRulesEngine.kt`** — `insights()` with 7+ rules:
  - [ ] Welcome/No Data (when no records exist)
  - [ ] Low Sleep (sleepHours < 6.0) → WARNING
  - [ ] Good Sleep (sleepHours >= 8.0) → INFO
  - [ ] Sleep + Fatigue combo → WARNING
  - [ ] Repeated Symptom (2+ → WARNING, 4+ → ALERT)
  - [ ] High Severity (severity >= 4) → ALERT
  - [ ] Multiple Symptoms (3+ active) → WARNING
  - [ ] Missed Check-In → INFO (checked before no-data guard)
  - [ ] Low Hydration (< 1000ml) → WARNING
  - [ ] Good Hydration (>= 2000ml) → INFO
  - [ ] High Stress (>= 7) → WARNING
- [ ] **`LocalRulesEngine.kt`** — `parseJournalNote()`:
  - [ ] 30+ symptom keywords with default severity
  - [ ] Severity modifier extraction (severe→5, mild→2, etc.)
  - [ ] Duration extraction ("for 3 days" → 72h, "since yesterday" → 24h)
  - [ ] Unknown text returns empty list
- [ ] **`SymptomJournalScreen.kt`**:
  - [ ] Natural language text input
  - [ ] Analyze button → parses via `parseJournalNote()`
  - [ ] Review list showing extracted name, severity, duration
  - [ ] "Save All to Symptoms" → creates SymptomEntity records
  - [ ] "Discard" → clears without saving
  - [ ] Example texts shown when empty
- [ ] **`LocalRulesEngineTest.kt`** — 20+ unit tests:
  - [ ] All rules tested with positive, negative, boundary cases
  - [ ] Parser tests: empty, single, multiple, severity modifiers, duration patterns
- [ ] **Command:** `cd apps/mobile && ./gradlew test` ✅ passes

### Level 4: Proactive Behavior — Notifications

- [ ] **`NotificationHelper.kt`**:
  - [ ] 3 notification channels: medication_reminders (HIGH), checkin_reminders (DEFAULT), general_reminders (LOW)
  - [ ] `showMedicationReminder()` — "Time to take [name] ([dose])"
  - [ ] `showCheckInReminder()` — "How are you feeling today?"
  - [ ] `hasPermission()` — checks POST_NOTIFICATIONS on Android 13+
- [ ] **`ReminderScheduler.kt`**:
  - [ ] `scheduleMedicationReminder()` — daily AlarmManager at specified hour/minute
  - [ ] `cancelMedicationReminders()` — cancels all pending intents for medication
  - [ ] `scheduleCheckInReminder()` — daily alarm at 10:00 AM
  - [ ] `cancelCheckInReminder()` — cancels check-in alarm
  - [ ] `MedicationReminderReceiver` BroadcastReceiver
  - [ ] `CheckInReminderReceiver` BroadcastReceiver
- [ ] **`AndroidManifest.xml`**:
  - [ ] Permissions: POST_NOTIFICATIONS, SCHEDULE_EXACT_ALARM, USE_EXACT_ALARM, WAKE_LOCK, INTERNET
  - [ ] `<receiver>` declarations for both BroadcastReceivers

### Level 5: Cloud — Sync Engine

- [ ] **`SyncApiClient.kt`**:
  - [ ] `upsertRecord()` — POST to `/v1/sync/upsert`
  - [ ] `batchUpsert()` — POST to `/v1/sync/batch`
  - [ ] `fetchRecords()` — GET from `/v1/sync/records/{userId}`
  - [ ] OkHttp with 30s timeouts
  - [ ] `withContext(Dispatchers.IO)` for all network calls
- [ ] **`RecordMapper.kt`**:
  - [ ] 6 mapper methods: checkInToPayload, symptomToPayload, medicationToPayload, profileToPayload, emergencyContactToPayload, doseEventToPayload
  - [ ] All produce correct DynamoDB key pattern: `PK=USER#<userId>`, `SK=<entity>#<timestamp>#<id>`
  - [ ] Typed inner payloads for each entity type
  - [ ] Envelope includes deviceId, syncVersion
- [ ] **`HealthSyncWorker.kt`**:
  - [ ] Collects pending records from all 6 entity types
  - [ ] Batch uploads via `SyncApiClient.batchUpsert()`
  - [ ] Marks records as synced after successful upload (`mark*Synced()`)
  - [ ] 3 retry attempts with exponential backoff (30s initial)
  - [ ] `requestSync()` companion method with `ExistingWorkPolicy.REPLACE`
- [ ] **`HealthDao.kt` sync methods**:
  - [ ] `pending*()` for all 6 entities
  - [ ] `mark*Synced()` for all 6 entities
  - [ ] Aggregated `pending*Count()` queries
- [ ] **Build dependencies** (`app/build.gradle.kts`):
  - [ ] `org.jetbrains.kotlin.plugin.serialization` plugin applied
  - [ ] `com.squareup.okhttp3:okhttp:4.12.0` dependency
  - [ ] `com.squareup.retrofit2:retrofit:2.11.0` dependency
  - [ ] `androidx.work:work-runtime-ktx:2.10.0` dependency
  - [ ] `androidx.navigation:navigation-compose:2.8.5` dependency

### Level 6: Web Dashboard — Visualization

- [ ] **Main dashboard** (`apps/web/src/app/page.tsx`):
  - [ ] Loading state → renders `DashboardSkeleton`
  - [ ] Error state → renders `ErrorState` with retry button
  - [ ] Success state → renders all data sections
  - [ ] Demo/Live toggle button in header
  - [ ] Data fetched via `useEffect` + `useCallback`
- [ ] **7 Components**:
  - [ ] `HealthScore.tsx` — conic gradient ring, score label, description
  - [ ] `SyncStatus.tsx` — 4-panel grid, pending count, last synced time
  - [ ] `DashboardSkeleton.tsx` — animated pulse placeholders
  - [ ] `EmptyState.tsx` — icon, title, description, reusable
  - [ ] `ErrorState.tsx` — AlertTriangle icon, message, retry button
  - [ ] `InsightCarousel.tsx` — insight cards with severity coloring
  - [ ] `WeeklyReportCard.tsx` — score, adherence, symptom summary, highlights, recommendations
- [ ] **API layer** (`lib/api.ts`):
  - [ ] `fetchDashboardData()` — 6 parallel requests with 10s timeout
  - [ ] `fetchWeeklyReport()` — report endpoint
  - [ ] `fetchInsights()` — insights endpoint
  - [ ] Graceful mock fallback on API failure
  - [ ] `DashboardState` discriminated union type
- [ ] **Cloud AI reports** (`lib/cloud-ai-reports.ts`):
  - [ ] `generateWeeklyReport()` — score avg, adherence rate, highlights, recommendations
  - [ ] `analyzeSymptomTrends()` — group by name, avg severity, trend detection
  - [ ] `logsAreDecreasing()` — first-half vs second-half severity comparison
- [ ] **Demo data**:
  - [ ] `lib/mock-data.ts` — basic demo (1 check-in, 2 symptoms, 2 meds)
  - [ ] `lib/enhanced-mock-data.ts` — rich demo (7-day history, 5 symptoms, dose events, profile, insights, report)
- [ ] **Responsive breakpoints**: sm (640px), lg (1024px), xl (1280px)
- [ ] **Command:** `npm run typecheck:web` ✅ passes
- [ ] **Command:** `npm run build:web` ✅ passes

### Level 7: Build & Deploy Verification

- [ ] **`npm run typecheck --workspace @nursy/shared`** — passes
- [ ] **`npm run typecheck:web`** — passes
- [ ] **`npm run build:web`** — passes
- [ ] **`cd apps/mobile && ./gradlew assembleDebug`** — builds successfully
- [ ] **`cd apps/mobile && ./gradlew test`** — tests pass
- [ ] **`adb install -r apps/mobile/app/build/outputs/apk/debug/app-debug.apk`** — installs on device
- [ ] **`npm run dev:web`** — starts at localhost:3000
- [ ] **Web dashboard renders** with all data sections visible
- [ ] **Demo/Live toggle** switches between mock and API mode
- [ ] **Responsive design** works on mobile, tablet, desktop widths

---

## 7. Development Log

### Build History

| Date | Version | Changes |
|---|---|---|
| 2026-06-18 | 0.1.0 | Initial implementation of all 8 phases. Room database (6 entities, 50+ DAO methods), 8 Compose screens with ViewModel, LocalRulesEngine with 7 rule types + NL parser, notification system with AlarmManager, sync engine with WorkManager + DynamoDB mapping, Next.js web dashboard with 7 components and API layer, cloud AI report generation, 20+ unit tests, enhanced demo seed data. |

### Known Issues

| Issue | Severity | Status |
|---|---|---|
| `fallbackToDestructiveMigration()` used — need proper Room Migration objects before production | Medium | Open |
| No actual backend server deployed (API endpoints not yet implemented) | High | Open |
| SyncApiClient base URL points to `api.nursyai.com` — needs real backend URL | Low | Open |
| Demo user ID hardcoded as `"demo-user"` — no real auth wired | Low | Open |

### Quick Troubleshooting

| Problem | Solution |
|---|---|
| `./gradlew` command not found | `cd apps/mobile && chmod +x gradlew` |
| APK install fails with `INSTALL_FAILED_UPDATE_INCOMPATIBLE` | `adb uninstall com.nursyai` then install again |
| Web build fails with module not found | `npm install` from project root |
| `npm run dev:web` port in use | `cd apps/web && npx next dev -p 4000` |
| Room database version mismatch | App will crash — uninstall and reinstall, or the `fallbackToDestructiveMigration()` handles it |
| Android Studio cannot sync Gradle | Check `ANDROID_HOME` environment variable is set correctly |
