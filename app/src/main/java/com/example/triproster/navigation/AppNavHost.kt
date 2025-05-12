package com.example.triproster.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.triproster.ui.theme.screens.*
import com.example.triproster.ui.theme.screens.home.HomeScreen
import com.example.triproster.ui.theme.screens.login.LoginScreen
import com.example.triproster.ui.theme.screens.register.RegisterScreen
import com.example.triproster.ui.theme.screens.summary.SummaryScreen
import com.example.triproster.ui.theme.screens.summary.EditSummaryScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(navController: NavHostController, startDestination: String = Routes.SPLASH) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }
        composable(Routes.HOME) {
            HomeScreen(navController)
        }
        composable(Routes.TRIP_LIST) {
            TripListScreen(navController = navController)
        }
        composable(Routes.ADD_TRIP) {
            AddTripScreen(navController)
        }
        composable(Routes.EDIT_TRIP) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            EditTripScreen(navController = navController, tripId = tripId)
        }
        composable(Routes.SUMMARY_SCREEN) {
            SummaryScreen(navController = navController)
        }

        // Add the EditSummaryScreen route
        composable("edit_summary_screen/{year}/{initialSummary}") { backStackEntry ->
            val year = backStackEntry.arguments?.getString("year") ?: ""
            val initialSummary = backStackEntry.arguments?.getString("initialSummary") ?: ""
            EditSummaryScreen(navController = navController, year = year, initialSummary = initialSummary)
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(navController)
        }

    }
}
