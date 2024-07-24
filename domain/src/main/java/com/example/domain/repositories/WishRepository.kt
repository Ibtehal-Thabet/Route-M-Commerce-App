package com.example.domain.repositories

import com.example.domain.common.ResultWrapper
import com.example.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface WishRepository {

    suspend fun addToWish(product: Product, authorization: String): Flow<ResultWrapper<List<String?>?>>

    suspend fun removeFromWish(product: Product, authorization: String): Flow<ResultWrapper<List<String?>?>>

    suspend fun getAuthWish(authorization: String): Flow<ResultWrapper<List<Product?>?>>
}