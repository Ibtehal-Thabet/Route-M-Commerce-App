package com.example.domain.useCases

import com.example.domain.common.ResultWrapper
import com.example.domain.model.Product
import com.example.domain.model.cart.CartItem
import com.example.domain.model.cart.CartProducts
import com.example.domain.repositories.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    //it will used in the view model
    suspend fun addToCart(cartItemId: String, authorization: String): Flow<ResultWrapper<CartProducts<String>?>> {
        return repository.addToCart(cartItemId, authorization)
    }

    suspend fun removeFromCart(cartItemId: String, authorization: String): Flow<ResultWrapper<CartProducts<Product>?>> {
        return repository.removeFromCart(cartItemId, authorization)
    }

    suspend fun getAuthCart(authorization: String): Flow<ResultWrapper<CartProducts<Product>?>> {
        return repository.getAuthCart(authorization)
    }

    suspend fun updateCartProductQuantity(cartItemId: String, quantity: String, authorization: String): Flow<ResultWrapper<CartProducts<Product>?>> {
        return repository.updateCartProductQuantity(cartItemId, quantity, authorization)
    }

    suspend fun removeAllCart(authorization: String): Flow<ResultWrapper<List<String?>?>> {
        return repository.removeAllCart(authorization)
    }

}