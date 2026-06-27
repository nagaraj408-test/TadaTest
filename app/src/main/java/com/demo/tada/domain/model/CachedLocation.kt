package com.demo.tada.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CachedLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val aqi: Int
) : Parcelable
