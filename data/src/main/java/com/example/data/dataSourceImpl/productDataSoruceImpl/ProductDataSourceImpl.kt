package com.example.data.dataSourceImpl.productDataSoruceImpl

import com.example.data.api.WebService
import com.example.data.dataSourceContract.ProductDataSource
import com.example.data.safeApiCall
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ProductDataSourceImpl @Inject constructor(
    private val webService: WebService
) : ProductDataSource {
    override suspend fun getProducts(categoryId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        val response = safeApiCall {
            webService.getProducts(category = categoryId, authorization = authorization)
        }
        return response
    }

    override suspend fun getSubCategoryProducts(subCategoryId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        val response = safeApiCall {
            webService.getProducts(subcategory = subCategoryId, authorization = authorization)
        }
        return response
    }

    override suspend fun getProduct(productId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        val response = safeApiCall {
            webService.getProducts(id = productId, authorization = authorization)
        }
        return response
    }

    override suspend fun getBrandProducts(brandId: String?, authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        val response = safeApiCall {
            webService.getProducts(brand = brandId, authorization = authorization)
        }
        return response
    }

    override suspend fun getSearchedProduct(authorization: String): Flow<ResultWrapper<List<Product?>?>> {
        val response = safeApiCall {
            webService.getProducts(authorization = authorization)
        }
        return response
    }
}