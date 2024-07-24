package com.example.data.repositoryImpl.subCategoryRepo

import com.example.domain.repositories.SubCategoriesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class di {

    @Binds
    abstract fun provideSubCategoriesRepository(
        subCategoryRepositoryImpl: SubCategoryRepositoryImpl
    ): SubCategoriesRepository

}
