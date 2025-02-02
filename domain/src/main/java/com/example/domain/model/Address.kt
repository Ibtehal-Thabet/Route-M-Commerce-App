package com.example.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val name: String? = null,
    val details: String? = null,
    val phone: String? = null,
    val city: String? = null,
    val _id: String? = null
): Parcelable