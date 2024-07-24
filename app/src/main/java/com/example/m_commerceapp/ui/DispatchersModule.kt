package com.example.m_commerceapp.ui

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Dispatcher
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @IODispatcher
    fun provideIODispatcher():CoroutineDispatcher= Dispatchers.IO

    @Provides
    @MainDispatcher
    fun provideMainDispatcher():CoroutineDispatcher= Dispatchers.Main

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class IODispatcher

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class MainDispatcher
}