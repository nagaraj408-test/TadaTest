package com.demo.tada.data.remote.api

import com.demo.tada.data.remote.dto.geocode.ReverseGeocodeResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ReverseGeocodeApi {

    @GET("data/reverse-geocode")
    suspend fun reverseGeocode(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("localityLanguage") language: String = "en",
        @Query("key") apiKey: String = com.demo.tada.utils.Constants.BDC_API_KEY
    ): ReverseGeocodeResponseDto
}