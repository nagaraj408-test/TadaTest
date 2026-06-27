package com.demo.tada.data.mapper

import com.demo.tada.data.remote.dto.booking.BookResponseDto
import com.demo.tada.domain.model.Book
import com.demo.tada.domain.model.LocationInfo

object BookMapper {

    fun toDomain(dto: BookResponseDto): Book {

        return Book(
            a = LocationInfo(
                latitude = dto.a.latitude,
                longitude = dto.a.longitude,
                aqi = dto.a.aqi,
                address = dto.a.name
            ),
            b = LocationInfo(
                latitude = dto.b.latitude,
                longitude = dto.b.longitude,
                aqi = dto.b.aqi,
                address = dto.b.name
            ),
            price = dto.price
        )
    }
}