package com.example.data.repositoryImpl.categoryRepo

import com.example.data.dataSourceContract.CategoryDataSource
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Category
import com.example.domain.repositories.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
     private val dataSource: CategoryDataSource
) : CategoriesRepository {
    override suspend fun getCategories(): Flow<ResultWrapper<List<Category?>?>> {
        return dataSource.getCategories()
    }
}