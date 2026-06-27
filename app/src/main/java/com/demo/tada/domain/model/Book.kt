package com.demo.tada.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val a: LocationInfo,
    val b: LocationInfo,
    val price: Double
) : Parcelable
