package com.example.data.dataSourceImpl.wishDataSourceImpl

import com.example.data.api.WebService
import com.example.data.dataSourceContract.WishListDataSource
import com.example.data.safeApiCall
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class WishListDataSourceImpl @Inject constructor(
    private val webService: WebService
) : WishListDataSource {

    override suspend fun addToWish(product: Product, authorization: String): Flow<ResultWrapper<List<String?>?>> {
        val response = safeApiCall {
            webService.addToWishList(productId = product._id?:"", authorization = authorization)
        }

        return response
    }

    override suspend fun removeFromWish(product: Product, authorization: String): Flow<ResultWrapper<List<String?>?>> {
        val response = safeApiCall {
            webService.removeFromWishList(productId = product._id?:"", authorization = authorization)
        }
        return response
    }

    override suspend fun getAuthWish(authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        val response = safeApiCall {
            webService.getAuthWishList(authorization = authorization)
        }
        return response
    }
}