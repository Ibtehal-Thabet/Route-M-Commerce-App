package com.example.data.dataSourceImpl.brandDataSoruceImpl

import com.example.data.dataSourceContract.BrandDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class BrandDi {

    @Binds
    abstract fun provideBrandDataSource(
        brandOnlineDataSourceImpl: BrandOnlineDataSourceImpl
    ): BrandDataSource
}