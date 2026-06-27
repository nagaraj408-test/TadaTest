package com.demo.tada.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.demo.tada.presentation.screen.booking.BookingScreen
import com.demo.tada.presentation.screen.history.HistoryScreen
import com.demo.tada.presentation.screen.map.MapScreen
import com.demo.tada.presentation.screen.nickname.NicknameScreen

@Composable
fun AppNavGraph(
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Map.route
    ) {

        composable(
            route = Screen.Map.route
        ) {
            MapScreen(navController = navController)
        }

        composable(
            route = Screen.Nickname.route
        ) {

            NicknameScreen(navController = navController)
        }

        composable(
            route = Screen.Booking.route
        ) {
            BookingScreen(navController = navController)
        }

        composable(
            route = Screen.History.route
        ) {
            HistoryScreen(navController = navController)
        }
    }
}