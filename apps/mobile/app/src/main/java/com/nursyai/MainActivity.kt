package com.nursyai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nursyai.backend.AppwriteBackend
import com.nursyai.navigation.Screen
import com.nursyai.ui.NursyViewModel
import com.nursyai.ui.screens.DailyCheckInScreen
import com.nursyai.ui.screens.DashboardScreen
import com.nursyai.ui.screens.EmergencyCardScreen
import com.nursyai.ui.screens.HealthTimelineScreen
import com.nursyai.ui.screens.MedicationManagementScreen
import com.nursyai.ui.screens.OfflineAiSettingsScreen
import com.nursyai.ui.screens.ProfileScreen
import com.nursyai.ui.screens.SymptomJournalScreen
import com.nursyai.ui.screens.SymptomTrackingScreen
import com.nursyai.ui.theme.NursyColors
import com.nursyai.ui.theme.NursyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppwriteBackend.init(applicationContext)
        lifecycleScope.launch {
            runCatching { AppwriteBackend.ping() }
        }
        setContent {
            NursyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = NursyColors.background
                ) {
                    NursyApp()
                }
            }
        }
    }
}

// Simple icon representation for navigation
data class NavIcon(val filled: String, val outline: String)

private val navIcons = mapOf(
    Screen.Dashboard.route to NavIcon("🏠", "🏠"),
    Screen.DailyCheckIn.route to NavIcon("📝", "📝"),
    Screen.SymptomTracking.route to NavIcon("🔍", "🔍"),
    Screen.MedicationManagement.route to NavIcon("💊", "💊")
)

@Composable
fun NursyApp() {
    val navController = rememberNavController()
    val viewModel: NursyViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavScreens = listOf(
        Screen.Dashboard,
        Screen.DailyCheckIn,
        Screen.SymptomTracking,
        Screen.MedicationManagement
    )

    val showBottomBar = currentDestination?.route in bottomNavScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    bottomNavScreens.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Text(
                                    text = navIcons[screen.route]?.filled ?: "•",
                                    fontSize = 20.sp
                                )
                            },
                            label = {
                                Text(
                                    text = screen.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NursyColors.moss,
                                selectedTextColor = NursyColors.moss,
                                unselectedIconColor = NursyColors.inkMuted,
                                unselectedTextColor = NursyColors.inkMuted,
                                indicatorColor = NursyColors.cloud
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onOpenTimeline = { navController.navigate(Screen.HealthTimeline.route) },
                    onOpenProfile = { navController.navigate(Screen.Profile.route) },
                    onOpenEmergency = { navController.navigate(Screen.EmergencyCard.route) },
                    onOpenJournal = { navController.navigate(Screen.SymptomJournal.route) },
                    onOpenOfflineAi = { navController.navigate(Screen.OfflineAiSettings.route) }
                )
            }
            composable(Screen.DailyCheckIn.route) {
                DailyCheckInScreen(viewModel = viewModel)
            }
            composable(Screen.SymptomTracking.route) {
                SymptomTrackingScreen(viewModel = viewModel)
            }
            composable(Screen.MedicationManagement.route) {
                MedicationManagementScreen(viewModel = viewModel)
            }
            composable(Screen.HealthTimeline.route) {
                HealthTimelineScreen(viewModel = viewModel)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(viewModel = viewModel)
            }
            composable(Screen.EmergencyCard.route) {
                EmergencyCardScreen(viewModel = viewModel)
            }
            composable(Screen.SymptomJournal.route) {
                SymptomJournalScreen(viewModel = viewModel)
            }
            composable(Screen.OfflineAiSettings.route) {
                OfflineAiSettingsScreen(viewModel = viewModel)
            }
        }
    }
}
