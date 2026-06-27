package com.demo.tada.presentation.screen.history.viewmodel

import com.demo.tada.domain.model.Book

data class HistoryUiState(
    val bookings: List<Book> = emptyList(),
    val totalCount: Int = 0,
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)
