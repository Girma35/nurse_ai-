---
name: nursy-mobile-app-shell
description: Build or modify the Nursy AI Android mobile app shell, Jetpack Compose navigation, bottom nav bar, screen routing, app scaffold, offline-first mobile UX, dashboard entry points, or emergency card access. Use when working on the main mobile user experience.
---

# Nursy Mobile App Shell

## Objective

Build the Android app as the primary Nursy AI product. The app uses Jetpack Compose with navigation, a shared `NursyViewModel`, and offline-first screens.

## Architecture

```text
MainActivity.kt
  -> NursyApp()
     -> Scaffold(bottomBar)
     -> NavHost(startDestination = "dashboard")
        -> DashboardScreen
        -> DailyCheckInScreen
        -> SymptomTrackingScreen
        -> MedicationManagementScreen
        -> HealthTimelineScreen
        -> ProfileScreen
        -> EmergencyCardScreen
        -> SymptomJournalScreen

NursyViewModel
  -> Room DAO flows
  -> local rules
  -> sync triggers
```

## Start Here

- Read `README.md` and `component` for product scope.
- Inspect `apps/mobile/app/src/main/java/com/nursyai/MainActivity.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/navigation/NavigationRoutes.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/theme/Theme.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt`
- Inspect `apps/mobile/app/build.gradle.kts`

## Implementation Guidance

- Treat Android as the only product surface.
- Keep the emergency card reachable without network.
- Keep core tabs focused on repeated daily use.
- Reuse Room, sync, and local rules packages instead of parallel state layers.
- Use existing Compose style and `NursyColors`.

## Routes

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

## Verify

- Confirm the project syncs in Android Studio.
- Confirm bottom nav selection state toggles correctly.
- Confirm no-network path: launch, save local record, reopen app, data persists.
- Run `cd apps/mobile && ./gradlew assembleDebug`.
