package com.example.domain.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class User(
    val createdAt: String? = null,
    val name: String? = null,
    val _id: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val password: String? = null,
    val rePassword: String? = null,
    var token: String? = null
) : Parcelable

@Parcelize
data class LoginRequest(val email: String, val password: String): Parcelable
