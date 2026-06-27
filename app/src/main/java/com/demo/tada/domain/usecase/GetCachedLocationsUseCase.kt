package com.demo.tada.domain.usecase

import com.demo.tada.domain.model.CachedLocation
import com.demo.tada.domain.repository.BookingRepository
import javax.inject.Inject

class GetCachedLocationsUseCase @Inject constructor(
    private val repository: BookingRepository
) {

    suspend operator fun invoke(): List<CachedLocation> {

        return repository.getCachedLocations()
    }
}