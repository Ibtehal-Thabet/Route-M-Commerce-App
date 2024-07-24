package com.example.data.repositoryImpl.wishListRepo

import com.example.data.dataSourceContract.WishListDataSource
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Product
import com.example.domain.repositories.WishRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WishRepositoryImpl @Inject constructor(
    private val dataSource: WishListDataSource
) : WishRepository {

    override suspend fun addToWish(product: Product, authorization: String): Flow<ResultWrapper<List<String?>?>> {
        return dataSource.addToWish(product, authorization)
    }

    override suspend fun removeFromWish(product: Product, authorization: String): Flow<ResultWrapper<List<String?>?>> {
        return dataSource.removeFromWish(product, authorization)
    }

    override suspend fun getAuthWish(authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        return dataSource.getAuthWish(authorization)
    }
}