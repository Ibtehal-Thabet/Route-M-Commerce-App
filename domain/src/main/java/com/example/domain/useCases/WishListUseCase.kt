package com.example.domain.useCases

import com.example.domain.common.ResultWrapper
import com.example.domain.model.Product
import com.example.domain.repositories.WishRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WishListUseCase @Inject constructor(
    private val repository: WishRepository
) {
    //it will used in the view model
    suspend fun addToWishList(product: Product, authorization: String): Flow<ResultWrapper<List<String?>?>> {
        return repository.addToWish(product, authorization)
    }

    suspend fun removeFromWishList(product: Product, authorization: String): Flow<ResultWrapper<List<String?>?>> {
        return repository.removeFromWish(product, authorization)
    }

    suspend fun getAuthWishList(authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        return repository.getAuthWish(authorization)
    }

}