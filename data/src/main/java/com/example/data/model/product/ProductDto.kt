package com.example.data.model.product

import com.google.gson.annotations.SerializedName

data class ProductDto(

    @field:SerializedName("_id")
    val _id: String? = null,

    @field:SerializedName("images")
    val images: List<String?>? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("price")
    val price: Int? = null,

    @field:SerializedName("ratingsAverage")
    val ratingsAverage: Double? = null,

    @field:SerializedName("slug")
    val slug: String? = null,

    @field:SerializedName("sold")
    val sold: Int? = null,

    @field:SerializedName("quantity")
    val quantity: Int? = null,

    @field:SerializedName("availableColors")
    val availableColors: List<Any?>? = null,

    @field:SerializedName("imageCover")
    val imageCover: String? = null,

    @field:SerializedName("ratingsQuantity")
    val ratingsQuantity: Int? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("subcategory")
    val subcategory: List<SubcategoryItem?>? = null,

    @field:SerializedName("category")
    val category: com.example.domain.model.Category? = null,

    @field:SerializedName("brand")
    val brand: Brand? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null,

    @field:SerializedName("priceAfterDiscount")
    val priceAfterDiscount: Int? = null
) {
    fun toProduct(): com.example.domain.model.Product {
        return com.example.domain.model.Product(
            images = images,
            imageCover = imageCover,
            title = title,
            description = description,
            price = price,
            ratingsAverage = ratingsAverage,
            category = category
        )
    }
}