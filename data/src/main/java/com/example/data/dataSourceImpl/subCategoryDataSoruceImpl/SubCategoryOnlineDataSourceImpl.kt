package com.example.data.dataSourceImpl.subCategoryDataSoruceImpl

import android.util.Log
import com.example.data.api.WebService
import com.example.data.dataSourceContract.SubCategoryDataSource
import com.example.data.safeApiCall
import com.example.domain.common.ResultWrapper
import com.example.domain.model.SubCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class SubCategoryOnlineDataSourceImpl @Inject constructor(
    private val webService: WebService
    ) : SubCategoryDataSource {

    override suspend fun getSubCategories(categoryId: String?): Flow<ResultWrapper<List<SubCategory?>?>> {
        val response = safeApiCall {
            webService.getSubCategoriesFromCategory(category = categoryId)
        }
        return response
    }
}