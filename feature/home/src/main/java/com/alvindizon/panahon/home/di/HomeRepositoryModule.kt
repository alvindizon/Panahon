package com.alvindizon.panahon.home.di

import com.alvindizon.panahon.home.data.HomeRepository
import com.alvindizon.panahon.home.data.HomeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeRepositoryModule {

    @Binds
    abstract fun bindHomeRepo(repo: HomeRepositoryImpl): HomeRepository
}
