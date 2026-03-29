package com.aistrategist.app.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Pulse : Screen("pulse")
    object Audit : Screen("audit")
    object Strategy : Screen("strategy")
    object Profile : Screen("profile")
}
