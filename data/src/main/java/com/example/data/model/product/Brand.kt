package com.example.data.model.product

import com.google.gson.annotations.SerializedName

data class Brand(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("_id")
    val _id: String? = null,

    @field:SerializedName("slug")
    val slug: String? = null
)