package com.demo.tada.domain.model

data class CachedLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val aqi: Int
)