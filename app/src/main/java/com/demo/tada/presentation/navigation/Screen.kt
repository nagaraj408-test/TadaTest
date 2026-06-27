package com.demo.tada.presentation.navigation

sealed class Screen(
    val route: String
) {

    data object Map : Screen(
        route = "map"
    )

    data object LocationDetail : Screen(
        route = "location_detail/{type}"
    ) {

        fun createRoute(
            type: String
        ): String {

            return "location_detail/$type"
        }
    }

    data object Booking : Screen(
        route = "booking"
    )

    data object History : Screen(
        route = "history"
    )

    data object Nickname : Screen(
        route = "nickname/{type}"
    ) {

        fun createRoute(
            type: String
        ): String {

            return "nickname/$type"
        }
    }
}