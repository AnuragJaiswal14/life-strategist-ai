package com.aistrategist.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aistrategist.app.presentation.dashboard.DashboardScreen
import com.aistrategist.app.presentation.chat.PulseScreen
import com.aistrategist.app.presentation.report.ReportScreen // Used as Strategy
import com.aistrategist.app.presentation.audit.AuditScreen
import com.aistrategist.app.presentation.login.LoginScreen
import com.aistrategist.app.presentation.profile.ProfileScreen
import com.aistrategist.app.presentation.forge.HabitForgeScreen

@Composable
fun AiStrategistNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { 
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToLog = { navController.navigate(Screen.Pulse.route) },
                onNavigateToAudit = { navController.navigate(Screen.Audit.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToStrategy = { navController.navigate("strategy") },
                onNavigateToForge = { navController.navigate(Screen.HabitForge.route) }
            )
        }
        composable(route = Screen.Pulse.route) {
            PulseScreen(
                onNavigateToDashboard = { navController.navigate(Screen.Dashboard.route) }
            )
        }
        composable(route = Screen.Audit.route) {
            AuditScreen(
                onBack = { navController.popBackStack() },
                onNavigateToStrategy = { appName -> 
                    if (appName == null) {
                        navController.navigate("strategy")
                    } else {
                        navController.navigate(Screen.Strategy.createRoute(appName))
                    }
                }
            )
        }
        composable(
            route = Screen.Strategy.route,
            arguments = listOf(androidx.navigation.navArgument("appName") { nullable = true; defaultValue = null })
        ) { backStackEntry ->
            val appName = backStackEntry.arguments?.getString("appName")
            ReportScreen(
                onBack = { navController.popBackStack() }
                // NOTE: Will pass appName if ReportScreen is physically updated later
            )
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.HabitForge.route) {
            HabitForgeScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
