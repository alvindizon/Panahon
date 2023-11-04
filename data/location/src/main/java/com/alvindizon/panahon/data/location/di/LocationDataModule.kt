package com.alvindizon.panahon.data.location.di

import com.alvindizon.panahon.data.location.HomeRepository
import com.alvindizon.panahon.data.location.HomeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationDataModule {

    @Binds
    abstract fun bindHomeRepo(repo: HomeRepositoryImpl): HomeRepository
}