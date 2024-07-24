package com.example.data.dataSourceImpl.categoryDataSoruceImpl

import com.example.data.api.WebService
import com.example.data.dataSourceContract.CategoryDataSource
import com.example.data.safeApiCall
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class CategoryOnlineDataSourceImpl @Inject constructor(
    private val webService: WebService
    ) : CategoryDataSource {
    override suspend fun getCategories(): Flow<ResultWrapper<List<Category?>?>> {
        val response = safeApiCall {
            webService.getCategories()
        }
        return response
    }
}