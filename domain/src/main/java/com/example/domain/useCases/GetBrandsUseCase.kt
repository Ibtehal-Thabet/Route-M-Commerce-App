package com.example.domain.useCases

import com.example.domain.common.ResultWrapper
import com.example.domain.model.Brand
import com.example.domain.repositories.BrandsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBrandsUseCase @Inject constructor(
    private val repository: BrandsRepository
        ) {
    //it will used in the view model
    suspend fun invokeBrands(): Flow<ResultWrapper<List<Brand?>?>> {
        return repository.getBrands()
    }
}