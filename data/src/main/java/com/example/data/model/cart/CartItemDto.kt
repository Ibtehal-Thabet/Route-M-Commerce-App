package com.example.data.model.cart

import com.example.domain.model.Product
import com.google.gson.annotations.SerializedName

data class CartItemDto(

	@field:SerializedName("_id")
	val _id: String? = null,

	@field:SerializedName("product")
	val product: Product? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("price")
	val price: Int? = null

)