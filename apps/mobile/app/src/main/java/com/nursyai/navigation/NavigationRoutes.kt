package com.nursyai.navigation

sealed class Screen(val route: String, val label: String) {
    data object Dashboard : Screen("dashboard", "Dashboard")
    data object DailyCheckIn : Screen("checkin", "Check-In")
    data object SymptomTracking : Screen("symptoms", "Symptoms")
    data object MedicationManagement : Screen("medications", "Medications")
    data object HealthTimeline : Screen("timeline", "Timeline")
    data object Profile : Screen("profile", "Profile")
    data object EmergencyCard : Screen("emergency", "Emergency")
    data object SymptomJournal : Screen("symptom_journal", "Journal")

    companion object {
        val bottomNavItems = listOf(
            Dashboard,
            DailyCheckIn,
            SymptomTracking,
            MedicationManagement
        )

        val drawerNavItems = listOf(
            HealthTimeline,
            Profile,
            EmergencyCard,
            SymptomJournal
        )
    }
}
