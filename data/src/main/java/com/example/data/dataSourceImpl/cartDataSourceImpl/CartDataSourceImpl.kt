package com.example.data.dataSourceImpl.cartDataSourceImpl

import com.example.data.api.WebService
import com.example.data.dataSourceContract.CartDataSource
import com.example.data.safeApiCall
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Product
import com.example.domain.model.cart.CartProducts
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CartDataSourceImpl @Inject constructor(
    private val webService: WebService
) : CartDataSource {

    override suspend fun addToCart(cartItemId: String, authorization: String): Flow<ResultWrapper<CartProducts<String>?>> {
        val response = safeApiCall {
            webService.addToCart(productId = cartItemId, authorization = authorization)
        }

        return response
    }

    override suspend fun removeFromCart(cartItemId: String, authorization: String): Flow<ResultWrapper<CartProducts<Product>?>> {
        val response = safeApiCall {
            webService.removeFromCart(productId = cartItemId, authorization = authorization)
        }
        return response
    }

    override suspend fun getAuthCart(authorization: String): Flow<ResultWrapper<CartProducts<Product>?>> {
        val response = safeApiCall {
            webService.getAuthCart(authorization = authorization)
        }
        return response
    }

    override suspend fun updateCartProductQuantity(cartItemId: String, quantity: String, authorization: String
    ): Flow<ResultWrapper<CartProducts<Product>?>> {
        val response = safeApiCall {
            webService.updateCartProductQuantity(cartItemId, quantity, authorization = authorization)
        }
        return response
    }

    override suspend fun removeAllCart(authorization: String): Flow<ResultWrapper<List<String?>?>> {
        val response =safeApiCall {
            webService.removeAllCart(authorization = authorization)
        }
        TODO("Not yet implemented")
    }
}