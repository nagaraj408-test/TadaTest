package com.demo.tada.domain.usecase

import com.demo.tada.domain.model.CachedLocation
import com.demo.tada.domain.repository.BookingRepository
import javax.inject.Inject

class CacheLocationUseCase @Inject constructor(
    private val repository: BookingRepository
) {

    suspend operator fun invoke(
        location: CachedLocation
    ) {
        repository.cacheLocation(location)
    }
}