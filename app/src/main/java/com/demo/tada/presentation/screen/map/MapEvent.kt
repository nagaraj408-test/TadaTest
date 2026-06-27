package com.demo.tada.presentation.screen.map

sealed interface MapEvent {

    data class CameraMoved(
        val latitude: Double,
        val longitude: Double
    ) : MapEvent

    data object SetLocation : MapEvent

    data object BookClicked : MapEvent

    data object BookingNavigationHandled : MapEvent

    data object AClicked : MapEvent

    data object BClicked : MapEvent
}