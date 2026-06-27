package com.demo.tada.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationInfo(
    val latitude: Double,
    val longitude: Double,
    val aqi: Int,
    val address: String,
    val nickname: String? = null
) : Parcelable
