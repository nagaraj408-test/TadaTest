package com.demo.tada.data.repository

import com.demo.tada.data.mapper.BookMapper
import com.demo.tada.data.remote.api.AirQualityApi
import com.demo.tada.data.remote.api.BookingApi
import com.demo.tada.data.remote.api.ReverseGeocodeApi
import com.demo.tada.data.remote.dto.booking.BookLocationDto
import com.demo.tada.data.remote.dto.booking.BookRequestDto
import com.demo.tada.domain.model.Book
import com.demo.tada.domain.model.CachedLocation
import com.demo.tada.domain.model.LocationInfo
import com.demo.tada.domain.repository.BookingRepository
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val airQualityApi: AirQualityApi,
    private val reverseGeocodeApi: ReverseGeocodeApi,
    private val bookingApi: BookingApi
) : BookingRepository {

    private val cachedLocations = mutableListOf<CachedLocation>()

    override suspend fun getAirQuality(latitude: Double, longitude: Double): Int {

        return airQualityApi.getAirQuality(
            latitude = latitude,
            longitude = longitude,
            token = "BuildConfig.AQI_TOKEN"
        ).data.aqi
    }

    override suspend fun getAddress(
        latitude: Double, longitude: Double
    ): String? {

        val response = reverseGeocodeApi.reverseGeocode(
            latitude = latitude, longitude = longitude
        )

        return response.localityInfo?.administrative?.sortedByDescending { it.order }?.take(2)
            ?.reversed()?.joinToString(", ") { it.name }
    }


    override suspend fun createBooking(a: LocationInfo, b: LocationInfo): Book {

        val request = BookRequestDto(
            a = BookLocationDto(
                latitude = a.latitude,
                longitude = a.longitude,
                aqi = a.aqi,
                name = a.nickname ?: a.address
            ),

            b = BookLocationDto(
                latitude = b.latitude,
                longitude = b.longitude,
                aqi = b.aqi,
                name = b.nickname ?: b.address
            )
        )

        val response = bookingApi.createBooking(request)

        return BookMapper.toDomain(response)
    }

    override suspend fun getBookings(
        year: Int, month: Int
    ): List<Book> {

        return bookingApi.getBookings(
                year = year, month = month
            ).map(BookMapper::toDomain)
    }


    override suspend fun cacheLocation(
        location: CachedLocation
    ) {
        cachedLocations.add(location)
    }

    override suspend fun getCachedLocations(): List<CachedLocation> {
        return cachedLocations.toList()
    }
}