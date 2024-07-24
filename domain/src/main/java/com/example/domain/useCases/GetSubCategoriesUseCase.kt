package com.example.domain.useCases

import com.example.domain.common.ResultWrapper
import com.example.domain.model.Category
import com.example.domain.model.SubCategory
import com.example.domain.repositories.SubCategoriesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubCategoriesUseCase @Inject constructor(
    private val repository: SubCategoriesRepository) {

    //it will used in the view model
    suspend fun invokeSubCategory(categoryId: String?): Flow<ResultWrapper<List<SubCategory?>?>> {
        return repository.getSubCategories(categoryId)
    }
}