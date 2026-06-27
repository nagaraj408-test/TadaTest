package com.demo.tada.domain.usecase

import com.demo.tada.domain.repository.BookingRepository
import javax.inject.Inject


class GetAirQualityUseCase @Inject constructor(
    private val repository: BookingRepository
) {

    suspend operator fun invoke(
        latitude: Double,
        longitude: Double
    ): Int {

        return repository.getAirQuality(
            latitude,
            longitude
        )
    }
}