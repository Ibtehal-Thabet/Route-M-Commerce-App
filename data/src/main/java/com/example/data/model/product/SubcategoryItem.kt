package com.example.data.model.product

import com.google.gson.annotations.SerializedName

data class SubcategoryItem(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("_id")
    val _id: String? = null,

    @field:SerializedName("category")
    val category: String? = null,

    @field:SerializedName("slug")
    val slug: String? = null
)