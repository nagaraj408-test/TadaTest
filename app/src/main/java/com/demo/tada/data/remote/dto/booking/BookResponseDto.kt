package com.demo.tada.data.remote.dto.booking

data class BookResponseDto(
    val id: Int,
    val a: BookLocationDto,
    val b: BookLocationDto,
    val price: Double
)