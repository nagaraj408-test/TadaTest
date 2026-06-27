package com.demo.tada.presentation.screen.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.tada.domain.usecase.GetBookingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getBookingsUseCase: GetBookingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBookings()
    }

    private fun loadBookings() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-indexed

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                getBookingsUseCase(year, month)
            }.onSuccess { bookings ->
                _uiState.update {
                    it.copy(
                        bookings = bookings,
                        totalCount = bookings.size,
                        totalPrice = bookings.sumOf { book -> book.price },
                        isLoading = false
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message
                    )
                }
            }
        }
    }
}
