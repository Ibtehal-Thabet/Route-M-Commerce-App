package com.example.data.dataSourceImpl.userDataSoruceImpl

import com.example.data.dataSourceContract.UserDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class UserDi {

    @Binds
    abstract fun provideUserDataSource(
        userOnlineDataSourceImpl: UserOnlineDataSourceImpl
    ): UserDataSource
}