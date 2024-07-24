package com.example.data.repositoryImpl.brandRepo

import com.example.data.dataSourceContract.BrandDataSource
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Brand
import com.example.domain.repositories.BrandsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BrandRepositoryImpl @Inject constructor(
     private val dataSource: BrandDataSource
) : BrandsRepository {

    override suspend fun getBrands(): Flow<ResultWrapper<List<Brand?>?>> {
        return dataSource.getBrands()
    }
}