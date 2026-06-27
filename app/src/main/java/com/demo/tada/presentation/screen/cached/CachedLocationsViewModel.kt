package com.demo.tada.presentation.screen.cached

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.tada.domain.model.CachedLocation
import com.demo.tada.domain.usecase.GetCachedLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CachedLocationsViewModel @Inject constructor(
    private val getCachedLocationsUseCase: GetCachedLocationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CachedLocationsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCachedLocations()
    }

    private fun loadCachedLocations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val locations = getCachedLocationsUseCase()
            _uiState.update { it.copy(locations = locations, isLoading = false) }
        }
    }
}

data class CachedLocationsUiState(
    val locations: List<CachedLocation> = emptyList(),
    val isLoading: Boolean = false
)
