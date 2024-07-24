package com.example.data.dataSourceContract

import com.example.domain.common.ResultWrapper
import com.example.domain.model.SubCategory
import kotlinx.coroutines.flow.Flow

interface SubCategoryDataSource {

    suspend fun getSubCategories(categoryId: String?): Flow<ResultWrapper<List<SubCategory?>?>>
}