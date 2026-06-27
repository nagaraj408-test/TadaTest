package com.demo.tada.data.remote.api

import com.demo.tada.data.remote.dto.airquality.AirQualityResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AirQualityApi {

    @GET("feed/geo:{lat};{lng}/")
    suspend fun getAirQuality(
        @Path("lat") latitude: Double,
        @Path("lng") longitude: Double,
        @Query("token") token: String
    ): AirQualityResponseDto
}