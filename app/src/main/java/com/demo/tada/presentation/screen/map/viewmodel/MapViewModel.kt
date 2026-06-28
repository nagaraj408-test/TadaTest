package com.demo.tada.presentation.screen.map.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.tada.data.mapper.LocationMapper
import com.demo.tada.domain.model.LocationInfo
import com.demo.tada.domain.usecase.CacheLocationUseCase
import com.demo.tada.domain.usecase.CreateBookingUseCase
import com.demo.tada.domain.usecase.GetAddressUseCase
import com.demo.tada.domain.usecase.GetAirQualityUseCase
import com.demo.tada.presentation.screen.map.MapEvent
import com.demo.tada.presentation.screen.map.MapUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAirQualityUseCase: GetAirQualityUseCase,
    private val getAddressUseCase: GetAddressUseCase,
    private val createBookingUseCase: CreateBookingUseCase,
    private val cacheLocationUseCase: CacheLocationUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    private var aqiJob: Job? = null
    private var addressJob: Job? = null

    fun onEvent(event: MapEvent) {
        when (event) {

            // Handle Camera Moved Event
            is MapEvent.CameraMoved -> {
                _uiState.update {
                    it.copy(
                        currentLatitude = event.latitude,
                        currentLongitude = event.longitude,
                        currentAddress = "Searching...",
                        currentAqi = 0,
                        error = null
                    )
                }

                loadAirQuality(event.latitude, event.longitude)
                loadAddress(event.latitude, event.longitude)
            }


            // Handle Set Location Event
            MapEvent.SetLocation -> {
                setLocation()
            }


            // Handle A and B Clicked Event
            MapEvent.AClicked -> {
                if (uiState.value.aLocation != null) {
                    _uiState.update { it.copy(navigateToNickname = "A") }
                } else {
                    _uiState.update { it.copy(navigateToCachedLocations = "A") }
                }
            }

            // Handle A and B Clicked Event
            MapEvent.BClicked -> {
                if (uiState.value.bLocation != null) {
                    _uiState.update { it.copy(navigateToNickname = "B") }
                } else {
                    _uiState.update { it.copy(navigateToCachedLocations = "B") }
                }
            }

            // Handle Book Clicked Event
            MapEvent.BookClicked -> {
                if (uiState.value.aLocation != null && uiState.value.bLocation != null) {
                    _uiState.update { it.copy(navigateToBooking = true, error = null) }
                    createBooking()
                }
            }


            // Handle Navigation Handled Event
            MapEvent.BookingNavigationHandled -> {
                _uiState.update { it.copy(navigateToBooking = false) }
            }

            // Handle Navigation Handled Event
            MapEvent.NicknameNavigationHandled -> {
                _uiState.update { it.copy(navigateToNickname = null) }
            }

            // Handle Navigation Handled Event
            MapEvent.CachedLocationsNavigationHandled -> {
                _uiState.update { it.copy(navigateToCachedLocations = null) }
            }

            // Handle Reset Event
            MapEvent.Reset -> {
                _uiState.value = MapUiState()
            }
        }
    }

    // Load AQI for a given location
    private fun loadAirQuality(latitude: Double, longitude: Double) {
        aqiJob?.cancel()
        aqiJob = viewModelScope.launch {
            try {
                delay(300.milliseconds)
                val aqi = getAirQualityUseCase(latitude, longitude)
                _uiState.update { it.copy(currentAqi = aqi) }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    // Load Address for a given location
    private fun loadAddress(latitude: Double, longitude: Double) {
        addressJob?.cancel()
        addressJob = viewModelScope.launch {
            try {
                delay(300.milliseconds)
                val address = getAddressUseCase(latitude, longitude)
                Log.d("MapViewModel", "Address updated: $address")
                _uiState.update { it.copy(currentAddress = address ?: "Unknown Address") }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("MapViewModel", "Address fetch failed", e)
                _uiState.update { it.copy(currentAddress = "Network Error, please check yours connection") }
            }
        }
    }


    // Set Location from current location
    private fun setLocation() {
        viewModelScope.launch {
            val state = uiState.value
            try {
                val address = if (state.currentAddress == "Searching..." || state.currentAddress == "Searching location...") {
                    getAddressUseCase(state.currentLatitude, state.currentLongitude) ?: "Unknown"
                } else {
                    state.currentAddress
                }

                val location = LocationMapper.toLocationInfo(
                    latitude = state.currentLatitude,
                    longitude = state.currentLongitude,
                    aqi = state.currentAqi,
                    address = address
                )

                if (state.aLocation == null) {
                    _uiState.update { it.copy(aLocation = location) }
                } else if (state.bLocation == null) {
                    _uiState.update { it.copy(bLocation = location) }
                }

                // Cache the location
                cacheLocationUseCase(
                    com.demo.tada.domain.model.CachedLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        address = location.address,
                        aqi = location.aqi
                    )
                )

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setLocationFromCache(type: String, cachedLocation: com.demo.tada.domain.model.CachedLocation) {
        val location = LocationMapper.toLocationInfo(
            latitude = cachedLocation.latitude,
            longitude = cachedLocation.longitude,
            aqi = cachedLocation.aqi,
            address = cachedLocation.address
        )
        _uiState.update { state ->
            if (type == "A") {
                state.copy(aLocation = location)
            } else {
                state.copy(bLocation = location)
            }
        }
    }

    fun setLocationsFromHistory(a: LocationInfo, b: LocationInfo) {
        _uiState.update { state ->
            state.copy(
                aLocation = a,
                bLocation = b
            )
        }
        // Fetch fresh AQI for both
        loadAirQualityForLocation(a)
        loadAirQualityForLocation(b)
    }

    private fun loadAirQualityForLocation(location: LocationInfo) {
        viewModelScope.launch {
            try {
                val aqi = getAirQualityUseCase(location.latitude, location.longitude)
                _uiState.update { state ->
                    if (state.aLocation?.latitude == location.latitude && state.aLocation?.longitude == location.longitude) {
                        state.copy(aLocation = state.aLocation?.copy(aqi = aqi))
                    } else if (state.bLocation?.latitude == location.latitude && state.bLocation?.longitude == location.longitude) {
                        state.copy(bLocation = state.bLocation?.copy(aqi = aqi))
                    } else {
                        state
                    }
                }
            } catch (e: Exception) {
                // Ignore background AQI refresh errors
            }
        }
    }

    fun createBooking() {
        val state = uiState.value
        val a = state.aLocation ?: return
        val b = state.bLocation ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val book = createBookingUseCase(a, b)
                _uiState.update { it.copy(isLoading = false, bookingSummary = book) }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.update { it.copy(isLoading = false, error = e.message) }
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

    fun getButtonText(): String {
        val state = uiState.value
        return when {
            state.aLocation == null -> "Set A"
            state.bLocation == null -> "Set B"
            else -> "Book"
        }
    }
}
