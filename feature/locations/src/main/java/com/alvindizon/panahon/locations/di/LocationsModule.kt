package com.alvindizon.panahon.locations.di

import com.alvindizon.panahon.locations.data.LocationsViewRepository
import com.alvindizon.panahon.locations.data.LocationsViewRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationsModule {

    @Binds
    abstract fun bindLocationsViewRepo(repo: LocationsViewRepositoryImpl): LocationsViewRepository
}