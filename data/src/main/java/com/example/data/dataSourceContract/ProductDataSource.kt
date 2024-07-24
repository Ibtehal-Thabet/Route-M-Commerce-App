package com.example.data.dataSourceContract


import com.example.domain.common.ResultWrapper
import com.example.domain.model.Product
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Header


interface ProductDataSource {
    suspend fun getProducts(categoryId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>>

    suspend fun getSubCategoryProducts(subCategoryId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>>

    suspend fun getProduct(productId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>>

    suspend fun getBrandProducts(brandId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>>

    suspend fun getSearchedProduct(authorization: String): Flow<ResultWrapper<List<Product?>?>>
}