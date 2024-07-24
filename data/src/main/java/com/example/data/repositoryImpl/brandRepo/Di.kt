package com.example.data.repositoryImpl.brandRepo

import com.example.domain.repositories.BrandsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class di {

    @Binds
    abstract fun provideBradsRepository(
        brandRepositoryImpl: BrandRepositoryImpl
    ):BrandsRepository

}
