package com.example.domain.useCases

import com.example.domain.common.ResultWrapper
import com.example.domain.model.Category
import com.example.domain.model.Product
import com.example.domain.model.SubCategory
import com.example.domain.repositories.ProductsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    //those will used in the view model
    suspend fun invokeCategoryProducts(categoryId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        return repository.getProducts(categoryId = categoryId, authorization = authorization)
    }

    suspend fun invokeSubCategoryProducts(subCategoryId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        return repository.getSubCategoryProducts(subCategoryId = subCategoryId, authorization = authorization)
    }

    suspend fun invokeProduct(productId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        return repository.getProduct(productId = productId, authorization = authorization)
    }

    suspend fun invokeBrandProducts(brandId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        return repository.getBrandProducts(brandId = brandId, authorization = authorization)
    }

    suspend fun invokeSearchedProducts(authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        return repository.getSearchedProduct(authorization = authorization)
    }

}