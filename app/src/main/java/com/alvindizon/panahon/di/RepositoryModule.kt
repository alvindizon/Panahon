package com.alvindizon.panahon.di

import com.alvindizon.panahon.integration.LocationsViewRepositoryImpl
import com.alvindizon.panahon.locations.integration.LocationsViewRepository
import com.alvindizon.panahon.repo.PanahonRepo
import com.alvindizon.panahon.repo.PanahonRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindPanahonRepo(repo: PanahonRepoImpl): PanahonRepo

    @Binds
    abstract fun bindLocationsViewRepo(repo: LocationsViewRepositoryImpl): LocationsViewRepository
}
