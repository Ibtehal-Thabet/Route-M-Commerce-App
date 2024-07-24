package com.example.data.dataSourceContract

import com.example.domain.common.ResultWrapper
import com.example.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface WishListDataSource {

    suspend fun addToWish(product: Product, authorization: String): Flow<ResultWrapper<List<String?>?>>

    suspend fun removeFromWish(product: Product, authorization: String): Flow<ResultWrapper<List<String?>?>>

    suspend fun getAuthWish(authorization: String): Flow<ResultWrapper<List<Product?>?>>

}