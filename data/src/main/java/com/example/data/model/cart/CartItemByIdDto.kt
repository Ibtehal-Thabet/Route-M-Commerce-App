package com.example.data.model.cart

import com.google.gson.annotations.SerializedName

data class CartItemByIdDto(

	@field:SerializedName("_id")
	val _id: String? = null,

	@field:SerializedName("product")
	val productId: String? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("price")
	val price: Int? = null

)