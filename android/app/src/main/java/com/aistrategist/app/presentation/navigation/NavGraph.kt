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

@Composable
fun AiStrategistNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { 
                    navController.navigate(Screen.Pulse.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToLog = { navController.navigate(Screen.Pulse.route) },
                onNavigateToAudit = { navController.navigate(Screen.Audit.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
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
                onNavigateToStrategy = { navController.navigate(Screen.Strategy.route) }
            )
        }
        composable(route = Screen.Strategy.route) {
            ReportScreen(
                onBack = { navController.popBackStack() }
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
    }
}
