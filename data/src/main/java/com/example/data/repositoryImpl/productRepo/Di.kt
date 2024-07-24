package com.example.data.repositoryImpl.productRepo

import com.example.domain.repositories.ProductsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class Di {

    @Binds
    abstract fun provideProductsRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductsRepository

}
