package com.example.data.model.cart

import com.example.data.model.BaseResponse
import com.google.gson.annotations.SerializedName

data class CartResponse(

    @field:SerializedName("metadata")
	val metadata: Metadata? = null,

    @field:SerializedName("results")
	val results: Int? = null
):BaseResponse<List<CartItemDto?>?>()