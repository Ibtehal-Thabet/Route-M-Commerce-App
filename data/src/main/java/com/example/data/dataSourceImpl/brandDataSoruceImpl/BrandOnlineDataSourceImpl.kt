package com.example.data.dataSourceImpl.brandDataSoruceImpl

import com.example.data.api.WebService
import com.example.data.dataSourceContract.BrandDataSource
import com.example.data.safeApiCall
import com.example.domain.common.ResultWrapper
import com.example.domain.model.Brand
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class BrandOnlineDataSourceImpl @Inject constructor(
    private val webService: WebService
    ) : BrandDataSource {

    override suspend fun getBrands(): Flow<ResultWrapper<List<Brand?>?>> {
        val response = safeApiCall {
            webService.getBrands()
        }
        return response
    }
}