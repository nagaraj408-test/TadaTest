package com.demo.tada.data.repository

import android.util.Log
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

        // Check if we have cached value alreday
        val cached = findInCache(latitude, longitude)
        if (cached != null) {
            return cached.aqi
        }

        //if not cached value, fetch from API
        return airQualityApi.getAirQuality(
            latitude = latitude,
            longitude = longitude,
            token = com.demo.tada.utils.Constants.AQI_TOKEN
        ).data.aqi
    }

    override suspend fun getAddress(
        latitude: Double, longitude: Double
    ): String? {
        val cached = findInCache(latitude, longitude)
        if (cached != null) {
            return cached.address
        }

        val response = reverseGeocodeApi.reverseGeocode(
            latitude = latitude, longitude = longitude
        )

        Log.d("TAG", "TESTRESPONSE geo: $response")

        val values = response.localityInfo?.administrative
            ?.sortedByDescending { it.order }
            ?.take(2)
            ?.sortedBy { it.order }
            ?.joinToString(", ") { it.name }

        Log.d("TAG", "TESTRESPONSE geo value: $values")

        return values
    }

    private fun findInCache(lat: Double, lng: Double): CachedLocation? {
        return cachedLocations.find {
            kotlin.math.abs(it.latitude - lat) < 0.001 && kotlin.math.abs(it.longitude - lng) < 0.001
        }
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