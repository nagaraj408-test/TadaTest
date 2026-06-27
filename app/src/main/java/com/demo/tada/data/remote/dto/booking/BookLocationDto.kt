package com.demo.tada.data.remote.dto.booking


data class BookLocationDto(
    val latitude: Double,
    val longitude: Double,
    val aqi: Int,
    val name: String
)