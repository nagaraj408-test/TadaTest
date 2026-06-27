package com.demo.tada.presentation.screen.map

import com.demo.tada.domain.model.LocationInfo

data class MapUiState(

    val currentLatitude: Double = 0.0,

    val currentLongitude: Double = 0.0,

    val currentAqi: Int = 0,

    val aLocation: LocationInfo? = null,

    val bLocation: LocationInfo? = null,

    val isLoading: Boolean = false,

    val error: String? = null,

    val navigateToBooking: Boolean = false,

    val navigateToNickname: String? = null,

    val bookingSummary: com.demo.tada.domain.model.Book? = null
)