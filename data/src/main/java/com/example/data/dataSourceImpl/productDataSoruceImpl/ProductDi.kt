package com.example.data.dataSourceImpl.di

import com.example.data.dataSourceContract.ProductDataSource
import com.example.data.dataSourceImpl.productDataSoruceImpl.ProductDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ProductDi {

    @Binds
    abstract fun provideProductDataSource(
        productDataSourceImpl: ProductDataSourceImpl
    ): ProductDataSource
}