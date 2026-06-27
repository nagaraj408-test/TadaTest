package com.demo.tada.domain.usecase

import com.demo.tada.domain.model.Book
import com.demo.tada.domain.model.LocationInfo
import com.demo.tada.domain.repository.BookingRepository
import javax.inject.Inject

class CreateBookingUseCase @Inject constructor(
    private val repository: BookingRepository
) {

    suspend operator fun invoke(
        a: LocationInfo,
        b: LocationInfo
    ): Book {

        return repository.createBooking(
            a = a,
            b = b
        )
    }
}