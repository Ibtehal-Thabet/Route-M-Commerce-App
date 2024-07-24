package com.example.data.repositoryImpl.cartRepo

import com.example.domain.repositories.CartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class di {

    @Binds
    abstract fun provideCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository

}