package com.example.domain.useCases

import com.example.domain.common.ResultWrapper
import com.example.domain.model.Category
import com.example.domain.repositories.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoriesRepository
        ) {
    //it will used in the view model
    suspend fun invoke(): Flow<ResultWrapper<List<Category?>?>> {
        return repository.getCategories()
    }
}