package com.example.domain.model.cart

data class CartProducts<T>(
    val _id: String? = null,
    val products: List<CartItem<T>>? = null,
    val totalCartPrice: Int? = null,
    val cartOwner: String? = null
)