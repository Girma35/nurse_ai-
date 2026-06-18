---
name: nursy-phase-2-core-mobile-offline-experience
description: Execute Nursy AI Phase 2 core mobile offline experience work, including Jetpack Compose navigation with NavHost and bottom bar, 8 screens (Dashboard, Check-In, Symptoms, Medications, Timeline, Profile, Emergency Card, Symptom Journal), NursyViewModel shared state layer, and NursyColors theme.
---

# Nursy Phase 2 — Core Mobile Offline Experience

## Objective

Make the Android app genuinely useful without internet by connecting 8 Compose screens to local Room data via a shared ViewModel.

## What Was Built

### Navigation (`MainActivity.kt` + `NavigationRoutes.kt`)

- `Screen` sealed class with 8 routes: dashboard, checkin, symptoms, medications, timeline, profile, emergency, symptom_journal
- `NavHost` with `startDestination = "dashboard"`
- Bottom navigation bar with 4 primary tabs (Dashboard, Check-In, Symptoms, Medications)
- Emoji-based icons with `NursyColors.moss` selected state

### Screens (8 Compose composables)

| Screen | File | Features |
|---|---|---|
| `DashboardScreen` | `ui/screens/DashboardScreen.kt` | Health score ring, quick metrics (sleep/water/energy), insights list, active symptoms, active medications, empty state |
| `DailyCheckInScreen` | `ui/screens/DailyCheckInScreen.kt` | Mood dropdown, energy slider, sleep input, sleep quality dropdown, stress slider, water intake, notes, pre-fill from today |
| `SymptomTrackingScreen` | `ui/screens/SymptomTrackingScreen.kt` | Add symptom form (name, severity slider, duration, notes), symptom history list, resolve button |
| `MedicationManagementScreen` | `ui/screens/MedicationManagementScreen.kt` | Add medication form (name, dose, frequency, schedule), medication list, schedule doses, deactivate |
| `HealthTimelineScreen` | `ui/screens/HealthTimelineScreen.kt` | Chronological events from check-ins, symptoms, dose events; date grouping, colored dot indicators, empty state |
| `ProfileScreen` | `ui/screens/ProfileScreen.kt` | Full name, DOB, gender, weight, height, blood type, allergies, conditions form |
| `EmergencyCardScreen` | `ui/screens/EmergencyCardScreen.kt` | Emergency card display (coral themed), add emergency contact form, empty state |
| `SymptomJournalScreen` | `ui/screens/SymptomJournalScreen.kt` | Natural language input, analyze button, parsed symptom review list, save/discard, example texts |

### ViewModel (`NursyViewModel.kt`)

Shared `AndroidViewModel` with:
- `StateFlow` properties: `latestCheckIn`, `activeSymptoms`, `activeMedications`, `profile`, `insights`, `pendingSyncCounts`
- `Flow` properties: `checkIns`, `allSymptoms`, `allMedications`, `doseEvents`, `emergencyContacts`
- Action methods: `saveCheckIn()`, `addSymptom()`, `resolveSymptom()`, `addMedication()`, `deactivateMedication()`, `markDoseAsTaken()`, `saveProfile()`, `addEmergencyContact()`, `deleteEmergencyContact()`, `parseJournalNote()`, `saveParsedSymptoms()`, `scheduleDoseEventsForMedication()`
- `insights` StateFlow combines `latestCheckIn` + `activeSymptoms` via `LocalRulesEngine.insights()`
- Demo user ID: `"demo-user"`

### Theme (`ui/theme/Theme.kt`)

```kotlin
object NursyColors {
    val ink = Color(0xFF16211C)
    val moss = Color(0xFF285947)
    val mint = Color(0xFF7BD9A2)
    val coral = Color(0xFFEF7B63)
    val amber = Color(0xFFF4B860)
    val cloud = Color(0xFFEEF3EF)
    val background = Color(0xFFF7FAF7)
    val surface = Color.White
}
```

## Key Files

- `apps/mobile/app/src/main/java/com/nursyai/MainActivity.kt`
- `apps/mobile/app/src/main/java/com/nursyai/navigation/NavigationRoutes.kt`
- `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt`
- `apps/mobile/app/src/main/java/com/nursyai/ui/theme/Theme.kt`
- `apps/mobile/app/src/main/java/com/nursyai/ui/screens/*.kt` (8 screens)

## Guardrails

- Keep the app usable in airplane mode — all data comes from Room.
- Prefer local save success over cloud sync success in user-facing flows.
- Do not make the web app a dependency for mobile behavior.
- Keep screen scope practical — each screen is a single Compose file.

## Exit Criteria

- User can use all 8 screens offline.
- Dashboard reflects locally saved data from all entity types.
- Emergency card is available offline (no network call required).
- `NursyViewModel` survives configuration changes.

## Verify

- Run the Android app from Android Studio or compile with `./gradlew assembleDebug`.
- Manually save check-in, symptom, medication, and profile data offline.
- Confirm dashboard and timeline update from local data immediately.
- Confirm bottom nav selection state works correctly between tabs.
- Confirm pre-fill works for today's existing check-in.
