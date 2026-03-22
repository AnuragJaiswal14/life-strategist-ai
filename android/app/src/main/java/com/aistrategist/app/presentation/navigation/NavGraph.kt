package com.aistrategist.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aistrategist.app.presentation.dashboard.DashboardScreen
import com.aistrategist.app.presentation.log.LogScreen
import com.aistrategist.app.presentation.report.ReportScreen
import com.aistrategist.app.presentation.login.LoginScreen

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
                onNavigateToLog = { navController.navigate(Screen.Log.route) },
                onNavigateToReport = { navController.navigate(Screen.Report.route) }
            )
        }
        composable(route = Screen.Log.route) {
            LogScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(route = Screen.Report.route) {
            ReportScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
