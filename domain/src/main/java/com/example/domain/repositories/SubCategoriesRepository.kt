package com.example.domain.repositories

import com.example.domain.common.ResultWrapper
import com.example.domain.model.SubCategory
import kotlinx.coroutines.flow.Flow

interface SubCategoriesRepository {
    suspend fun getSubCategories(categoryId: String?): Flow<ResultWrapper<List<SubCategory?>?>>
}