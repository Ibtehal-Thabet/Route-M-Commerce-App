package com.example.data.dataSourceImpl.cartDataSourceImpl

import com.example.data.dataSourceContract.CartDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class CartDi {

     @Binds
     abstract fun provideCartDataSource(
         cartDataSourceImpl: CartDataSourceImpl
     ): CartDataSource
}
