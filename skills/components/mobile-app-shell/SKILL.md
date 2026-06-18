---
name: nursy-mobile-app-shell
description: Build or modify the Nursy AI Android mobile app shell, Jetpack Compose navigation, bottom nav bar, screen routing, app scaffold, offline-first mobile UX, dashboard entry points, or emergency card access. Use when working on the main mobile user experience.
---

# Nursy Mobile App Shell

## Objective

Build the Android app as the primary Nursy AI product. The app uses Jetpack Compose with a bottom navigation bar, NavHost routing, and a shared ViewModel (`NursyViewModel`) that all screens consume. Keep the experience offline-first and make daily health tracking reachable with minimal navigation.

## Architecture Overview

```
MainActivity.kt
  └─ NursyApp()                         ← Compose root
       ├─ Scaffold(bottomBar)            ← Bottom navigation bar
       │    ├─ Dashboard (route: "dashboard")
       │    ├─ DailyCheckIn ("checkin")
       │    ├─ SymptomTracking ("symptoms")
       │    └─ MedicationManagement ("medications")
       └─ NavHost(startDestination = "dashboard")
            ├─ DashboardScreen
            ├─ DailyCheckInScreen
            ├─ SymptomTrackingScreen
            ├─ MedicationManagementScreen
            ├─ HealthTimelineScreen
            ├─ ProfileScreen
            ├─ EmergencyCardScreen
            └─ SymptomJournalScreen

NursyViewModel (AndroidViewModel)
  └─ Connects all screens to Room via HealthDao
```

## Start Here

- Read `component` and `README.md` for product scope.
- Inspect `apps/mobile/app/src/main/java/com/nursyai/MainActivity.kt` — the root composable with Scaffold, bottom nav, and NavHost.
- Inspect `apps/mobile/app/src/main/java/com/nursyai/navigation/NavigationRoutes.kt` — sealed class with `Screen.Dashboard`, `Screen.DailyCheckIn`, etc.
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/theme/Theme.kt` — `NursyColors` (ink, moss, mint, coral, amber, cloud, background).
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt` — the shared AndroidViewModel.
- Inspect `apps/mobile/app/build.gradle.kts` for Compose, Navigation, and Room dependencies.
- Reuse local Room, sync, and rules packages instead of inventing parallel app layers.

## Implementation Guidance

- **Treatment**: Treat mobile as the main product and web as a secondary dashboard.
- **Navigation**: Uses `androidx.navigation:navigation-compose:2.8.5`. Bottom nav bar with 4 primary tabs (Dashboard, Check-In, Symptoms, Medications). Remaining screens (Timeline, Profile, Emergency Card, Symptom Journal) are accessible from dashboard or drawer.
- **State layer**: `NursyViewModel` (AndroidViewModel) holds all observable `StateFlow` properties from Room DAO Flows. All 8 screens consume these via `collectAsState()`.
- **Bottom nav icons**: Emoji-based icons defined in `MainActivity.kt` as `NavIcon` data class. Selected state uses `NursyColors.moss`, unselected uses `NursyColors.inkMuted`.
- **Emergency card access**: Keep fast and available without network access — data comes from `ProfileEntity` and `EmergencyContactEntity` in Room.

## Concrete Route Definitions

```kotlin
sealed class Screen(val route: String, val label: String) {
    data object Dashboard : Screen("dashboard", "Dashboard")
    data object DailyCheckIn : Screen("checkin", "Check-In")
    data object SymptomTracking : Screen("symptoms", "Symptoms")
    data object MedicationManagement : Screen("medications", "Medications")
    data object HealthTimeline : Screen("timeline", "Timeline")
    data object Profile : Screen("profile", "Profile")
    data object EmergencyCard : Screen("emergency", "Emergency")
    data object SymptomJournal : Screen("symptom_journal", "Journal")
}
```

## Expected Deliverables

- Compose navigation structure with `NavHost` and 8 routes.
- Bottom navigation bar with 4 tabs.
- `NursyViewModel` shared across all screens.
- `NursyColors` theme constants.
- All 8 screen composables with offline-first behavior.

## Verify

- Confirm `npm run typecheck:web` passes if shared model changes affect web.
- Confirm the mobile project syncs in Android Studio (sync Gradle).
- Manually trace the no-network user path: launch → save a local health record → reopen app → data persists.
- Confirm bottom nav selection state toggles correctly between tabs.
- Confirm `NursyViewModel` survives configuration changes (screen rotation, process recreation).
