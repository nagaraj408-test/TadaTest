package com.demo.tada.domain.repository

import com.demo.tada.domain.model.Book
import com.demo.tada.domain.model.CachedLocation
import com.demo.tada.domain.model.LocationInfo

interface BookingRepository {

    suspend fun getAirQuality(
        latitude: Double,
        longitude: Double
    ): Int

    suspend fun getAddress(
        latitude: Double,
        longitude: Double
    ): String?

    suspend fun createBooking(
        a: LocationInfo,
        b: LocationInfo
    ): Book

    suspend fun getBookings(
        year: Int,
        month: Int
    ): List<Book>

    suspend fun cacheLocation(
        location: CachedLocation
    )

    suspend fun getCachedLocations(): List<CachedLocation>
}