package com.example.data.dataSourceImpl.subCategoryDataSoruceImpl

import com.example.data.dataSourceContract.SubCategoryDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class SubCategoryDi {

    @Binds
    abstract fun provideCategoryDataSource(
        subCategoryOnlineDataSourceImpl: SubCategoryOnlineDataSourceImpl
    ): SubCategoryDataSource
}