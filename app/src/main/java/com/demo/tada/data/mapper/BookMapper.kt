package com.demo.tada.data.mapper

import com.demo.tada.data.remote.dto.booking.BookResponseDto
import com.demo.tada.domain.model.Book

object BookMapper {

    fun toDomain(dto: BookResponseDto): Book {
        return Book(
            a = LocationMapper.fromDto(dto.a),
            b = LocationMapper.fromDto(dto.b),
            price = dto.price
        )
    }
}
