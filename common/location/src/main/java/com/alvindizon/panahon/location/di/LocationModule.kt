package com.alvindizon.panahon.location.di

import com.alvindizon.panahon.location.LocationManager
import com.alvindizon.panahon.location.LocationManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    abstract fun bindLocationManager(locationManager: LocationManagerImpl): LocationManager
}
