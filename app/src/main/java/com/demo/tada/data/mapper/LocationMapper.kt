package com.demo.tada.data.mapper

import com.demo.tada.data.remote.dto.booking.BookLocationDto
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

    fun fromDto(dto: BookLocationDto): LocationInfo {
        return LocationInfo(
            latitude = dto.latitude,
            longitude = dto.longitude,
            aqi = dto.aqi,
            address = dto.name
        )
    }
}
