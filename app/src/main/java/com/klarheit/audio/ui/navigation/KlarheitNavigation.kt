package com.klarheit.audio.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.klarheit.audio.ui.screens.main.MainScreen

@Composable
fun KlarheitNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main",
    ) {
        composable("main") {
            MainScreen()
        }
    }
}
