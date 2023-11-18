package com.alvindizon.panahon.data.location.di

import com.alvindizon.panahon.data.location.LocationRepository
import com.alvindizon.panahon.data.location.LocationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationDataModule {

    @Binds
    abstract fun bindHomeRepo(repo: LocationRepositoryImpl): LocationRepository
}