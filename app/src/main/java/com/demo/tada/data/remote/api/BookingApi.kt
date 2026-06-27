package com.demo.tada.data.remote.api

import com.demo.tada.data.remote.dto.booking.BookRequestDto
import com.demo.tada.data.remote.dto.booking.BookResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BookingApi {

    @POST("books")
    suspend fun createBooking(
        @Body request: BookRequestDto
    ): BookResponseDto

    @GET("books")
    suspend fun getBookings(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): List<BookResponseDto>
}