package com.example.data.model

import com.google.gson.annotations.SerializedName

open class BaseResponse<T>(
    @SerializedName("statusMsg")
    val statusMessage: String?=null,
    @SerializedName("message")
    val message: String?= null,
    @SerializedName("data")
    val data: T?= null,
    @SerializedName("numOfCartItems")
    val numOfCartItems: Int?= null
)