package com.example.data.model.cart

import com.google.gson.annotations.SerializedName

data class CartProductsDto(

    @field:SerializedName("_id")
    val _id: String? = null,

    @field:SerializedName("products")
    val products: List<CartItemDto>? = null,

    @field:SerializedName("count")
    val cartOwner: String? = null,

    @field:SerializedName("price")
    val totalCartPrice: Int? = null
)