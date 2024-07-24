package com.example.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Product(
	val _id: String? = null,
	val title: String? = null,
	val description: String? = null,
	val images: List<String?>? = null,
	val imageCover: String? = null,
	val price: Int? = null,
	val priceAfterDiscount: Int? = null,
	val ratingsAverage: @RawValue Any? = null,
	val availableColors: @RawValue List<Any?>? = null,
	val sold: Int? = null,
	val quantity: Int? = null,
	val ratingsQuantity: Int? = null,
	val createdAt: String? = null,
	val subcategory: List<SubcategoryItem?>? = null,
	val category: Category? = null,
	val brand: Brand? = null,
	val slug: String? = null,
	val updatedAt: String? = null,
	var isInWishList: Boolean = false,
	var isInCart: Boolean = false
) : Parcelable

@Parcelize
data class SubcategoryItem(
	val name: String? = null,
	val id: String? = null,
	val category: String? = null,
	val slug: String? = null
) : Parcelable

