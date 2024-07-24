package com.example.data.repositoryImpl.productRepo

import com.example.data.dataSourceContract.ProductDataSource
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Product
import com.example.domain.repositories.ProductsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val dataSource: ProductDataSource
) : ProductsRepository {

    override suspend fun getProducts(categoryId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        return dataSource.getProducts(categoryId = categoryId, authorization = authorization)
    }

    override suspend fun getSubCategoryProducts(subCategoryId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        return dataSource.getSubCategoryProducts(subCategoryId = subCategoryId, authorization = authorization)
    }

    override suspend fun getProduct(productId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        return dataSource.getProduct(productId = productId, authorization = authorization)
    }

    override suspend fun getBrandProducts(brandId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        return dataSource.getBrandProducts(brandId = brandId, authorization = authorization)
    }

    override suspend fun getSearchedProduct(authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        return dataSource.getSearchedProduct(authorization = authorization)
    }
}