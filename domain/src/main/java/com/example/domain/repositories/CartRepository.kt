package com.example.domain.repositories

import com.example.domain.common.ResultWrapper
import com.example.domain.model.Product
import com.example.domain.model.cart.CartItem
import com.example.domain.model.cart.CartProducts
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    suspend fun addToCart(cartItemId: String, authorization: String): Flow<ResultWrapper<CartProducts<String>?>>

    suspend fun removeFromCart(cartItemId: String, authorization: String): Flow<ResultWrapper<CartProducts<Product>?>>

    suspend fun getAuthCart(authorization: String): Flow<ResultWrapper<CartProducts<Product>?>>

    suspend fun updateCartProductQuantity(cartItemId: String, quantity: String, authorization: String): Flow<ResultWrapper<CartProducts<Product>?>>

    suspend fun removeAllCart(authorization: String): Flow<ResultWrapper<List<String?>?>>
}