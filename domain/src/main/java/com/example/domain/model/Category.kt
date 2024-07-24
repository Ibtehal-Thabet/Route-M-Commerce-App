package com.example.domain.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Category(
	val _id: String? = null,
	val image: String? = null,
	val createdAt: String? = null,
	val name: String? = null,
	val slug: String? = null,
	val updatedAt: String? = null
) : Parcelable
