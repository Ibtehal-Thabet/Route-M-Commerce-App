package com.example.data.repositoryImpl.cartRepo

import com.example.data.dataSourceContract.CartDataSource
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Product
import com.example.domain.model.cart.CartItem
import com.example.domain.model.cart.CartProducts
import com.example.domain.repositories.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CartRepositoryImpl@Inject constructor(
    private val dataSource: CartDataSource
) : CartRepository {

    override suspend fun addToCart(cartItemId: String, authorization: String): Flow<ResultWrapper<CartProducts<String>?>> {
        return dataSource.addToCart(cartItemId, authorization)
    }

    override suspend fun removeFromCart(cartItemId: String, authorization: String): Flow<ResultWrapper<CartProducts<Product>?>> {
        return dataSource.removeFromCart(cartItemId, authorization)
    }

    override suspend fun getAuthCart(authorization: String): Flow<ResultWrapper<CartProducts<Product>?>> {
        return dataSource.getAuthCart(authorization)
    }

    override suspend fun updateCartProductQuantity(cartItemId: String, quantity: String, authorization: String): Flow<ResultWrapper<CartProducts<Product>?>> {
        return dataSource.updateCartProductQuantity(cartItemId, quantity, authorization)
    }

    override suspend fun removeAllCart(authorization: String): Flow<ResultWrapper<List<String?>?>> {
        return dataSource.removeAllCart(authorization)
    }
}