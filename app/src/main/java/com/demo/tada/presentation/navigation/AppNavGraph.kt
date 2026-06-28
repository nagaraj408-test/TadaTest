package com.demo.tada.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.demo.tada.presentation.screen.booking.BookingScreen
import com.demo.tada.presentation.screen.cached.CachedLocationsScreen
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

        // Map Screen
        composable(
            route = Screen.Map.route
        ) {
            MapScreen(navController = navController)
        }


        // Nickname Screen
        composable(
            route = Screen.Nickname.route,
            arguments = listOf(
                navArgument("type") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type")
            val mapBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Map.route)
            }
            NicknameScreen(
                navController = navController,
                type = type,
                mapBackStackEntry = mapBackStackEntry
            )
        }


        // Booking Screen
        composable(
            route = Screen.Booking.route
        ) {
            val mapBackStackEntry = remember(it) {
                navController.getBackStackEntry(Screen.Map.route)
            }
            BookingScreen(
                navController = navController,
                mapBackStackEntry = mapBackStackEntry
            )
        }



        // History Screen
        composable(
            route = Screen.History.route
        ) {
            HistoryScreen(navController = navController)
        }


        // Cached Locations Screen
        composable(
            route = Screen.CachedLocations.route,
            arguments = listOf(
                navArgument("type") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type")
            val mapBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Map.route)
            }
            CachedLocationsScreen(
                navController = navController,
                type = type,
                mapBackStackEntry = mapBackStackEntry
            )
        }
    }
}