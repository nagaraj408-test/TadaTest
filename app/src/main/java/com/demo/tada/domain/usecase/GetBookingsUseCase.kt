package com.demo.tada.domain.usecase

import com.demo.tada.domain.model.Book
import com.demo.tada.domain.repository.BookingRepository
import javax.inject.Inject

class GetBookingsUseCase @Inject constructor(
    private val repository: BookingRepository
) {

    suspend operator fun invoke(
        year: Int,
        month: Int
    ): List<Book> {

        return repository.getBookings(
            year = year,
            month = month
        )
    }
}