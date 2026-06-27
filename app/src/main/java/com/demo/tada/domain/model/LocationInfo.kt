package com.demo.tada.domain.model

data class LocationInfo(
    val latitude: Double,
    val longitude: Double,
    val aqi: Int,
    val address: String,
    val nickname: String? = null
)