package com.example.data.repositoryImpl.wishListRepo

import com.example.domain.repositories.WishRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class di {

    @Binds
    abstract fun provideWishListRepository(
        wishRepositoryImpl: WishRepositoryImpl
    ): WishRepository

}
