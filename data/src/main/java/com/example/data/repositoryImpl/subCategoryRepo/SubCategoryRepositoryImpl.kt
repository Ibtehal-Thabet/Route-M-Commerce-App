package com.example.data.repositoryImpl.subCategoryRepo

import com.example.data.dataSourceContract.SubCategoryDataSource
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Category
import com.example.domain.model.SubCategory
import com.example.domain.repositories.SubCategoriesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubCategoryRepositoryImpl @Inject constructor(
     private val dataSource: SubCategoryDataSource
) : SubCategoriesRepository {
    override suspend fun getSubCategories(categoryId: String?): Flow<ResultWrapper<List<SubCategory?>?>> {
        return dataSource.getSubCategories(categoryId = categoryId)
    }
}