package com.example.domain.model.cart


data class CartItem<T>(
    val _id: String? = null,
    val product: T? = null,
    val price: Int? = null,
    val count: Int? = null
)