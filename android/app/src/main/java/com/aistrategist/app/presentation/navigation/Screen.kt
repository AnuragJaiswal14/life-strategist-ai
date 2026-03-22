package com.aistrategist.app.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Log : Screen("log")
    object Report : Screen("report")
}
