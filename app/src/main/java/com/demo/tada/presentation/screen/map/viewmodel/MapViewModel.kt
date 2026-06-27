package com.demo.tada.presentation.screen.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.tada.domain.model.LocationInfo
import com.demo.tada.domain.usecase.CacheLocationUseCase
import com.demo.tada.domain.usecase.CreateBookingUseCase
import com.demo.tada.domain.usecase.GetAddressUseCase
import com.demo.tada.domain.usecase.GetAirQualityUseCase
import com.demo.tada.presentation.screen.map.MapEvent
import com.demo.tada.presentation.screen.map.MapUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAirQualityUseCase: GetAirQualityUseCase,
    private val getAddressUseCase: GetAddressUseCase,
    private val createBookingUseCase: CreateBookingUseCase,
    private val cacheLocationUseCase: CacheLocationUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: MapEvent) {
        when (event) {
            is MapEvent.CameraMoved -> {
                loadAirQuality(
                    latitude = event.latitude,
                    longitude = event.longitude
                )
            }

            MapEvent.SetLocation -> {
                setLocation()
            }

            MapEvent.AClicked -> {
                if (uiState.value.aLocation != null) {
                    _uiState.update { it.copy(navigateToNickname = "A") }
                }
            }

            MapEvent.BClicked -> {
                if (uiState.value.bLocation != null) {
                    _uiState.update { it.copy(navigateToNickname = "B") }
                }
            }

            MapEvent.BookClicked -> {
                if (uiState.value.aLocation != null && uiState.value.bLocation != null) {
                    _uiState.update { it.copy(navigateToBooking = true) }
                    createBooking()
                }
            }

            MapEvent.BookingNavigationHandled -> {
                _uiState.update { it.copy(navigateToBooking = false) }
            }

            MapEvent.NicknameNavigationHandled -> {
                _uiState.update { it.copy(navigateToNickname = null) }
            }

            MapEvent.Reset -> {
                _uiState.value = MapUiState()
            }
        }
    }

    fun createBooking() {
        val state = uiState.value
        val a = state.aLocation ?: return
        val b = state.bLocation ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                createBookingUseCase(a, b)
            }.onSuccess { book ->
                _uiState.update { it.copy(isLoading = false, bookingSummary = book) }
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false, error = throwable.message) }
            }
        }
    }

    fun setNickname(type: String, nickname: String) {
        _uiState.update { state ->
            if (type == "A") {
                state.copy(aLocation = state.aLocation?.copy(nickname = nickname))
            } else {
                state.copy(bLocation = state.bLocation?.copy(nickname = nickname))
            }
        }
    }

    private fun loadAirQuality(
        latitude: Double,
        longitude: Double
    ) {

        viewModelScope.launch {

            runCatching {

                getAirQualityUseCase(
                    latitude = latitude,
                    longitude = longitude
                )

            }.onSuccess { aqi ->

                _uiState.update {

                    it.copy(
                        currentLatitude = latitude,
                        currentLongitude = longitude,
                        currentAqi = aqi
                    )
                }

            }.onFailure { throwable ->

                _uiState.update {

                    it.copy(
                        error = throwable.message
                    )
                }
            }
        }
    }

    private fun setLocation() {

        viewModelScope.launch {

            val state = uiState.value

            runCatching {

                val address = getAddressUseCase(
                    state.currentLatitude,
                    state.currentLongitude
                ) ?: "Unknown"

                LocationInfo(
                    latitude = state.currentLatitude,
                    longitude = state.currentLongitude,
                    aqi = state.currentAqi,
                    address = address
                )

            }.onSuccess { location ->

                if (state.aLocation == null) {

                    _uiState.update {

                        it.copy(
                            aLocation = location
                        )
                    }

                } else if (state.bLocation == null) {

                    _uiState.update {

                        it.copy(
                            bLocation = location
                        )
                    }
                }

            }.onFailure { throwable ->

                _uiState.update {

                    it.copy(
                        error = throwable.message
                    )
                }
            }
        }
    }

    fun getButtonText(): String {
        val state = uiState.value
        return when {

            state.aLocation == null ->
                "Set A"

            state.bLocation == null ->
                "Set B"

            else ->
                "Book"
        }
    }
}