package com.example.domain.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class SubCategory(
	val name: String? = null,
	val _id: String? = null,
	val category: String? = null,
	val createdAt: String? = null,
	val slug: String? = null,
	val updatedAt: String? = null
) : Parcelable
