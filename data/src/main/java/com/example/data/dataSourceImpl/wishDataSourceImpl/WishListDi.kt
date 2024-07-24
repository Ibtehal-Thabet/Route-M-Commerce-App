package com.example.data.dataSourceImpl.wishDataSourceImpl

import com.example.data.dataSourceContract.WishListDataSource
import com.example.data.dataSourceImpl.wishDataSourceImpl.WishListDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class WishListDi {

    @Binds
    abstract fun provideWishListDataSource(
        wishListDataSourceImpl: WishListDataSourceImpl
    ): WishListDataSource
}