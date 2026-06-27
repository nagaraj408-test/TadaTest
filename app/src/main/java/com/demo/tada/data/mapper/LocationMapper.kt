package com.demo.tada.data.mapper

import com.demo.tada.domain.model.LocationInfo

object LocationMapper {

    fun toLocationInfo(
        latitude: Double,
        longitude: Double,
        aqi: Int,
        address: String,
        nickname: String? = null
    ): LocationInfo {

        return LocationInfo(
            latitude = latitude,
            longitude = longitude,
            aqi = aqi,
            address = address,
            nickname = nickname
        )
    }
}