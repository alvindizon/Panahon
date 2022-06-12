package com.alvindizon.panahon.di

import com.alvindizon.panahon.details.integration.DetailsViewRepository
import com.alvindizon.panahon.integration.DetailsViewRepositoryImpl
import com.alvindizon.panahon.integration.LocationsViewRepositoryImpl
import com.alvindizon.panahon.integration.SearchLocationViewRepositoryImpl
import com.alvindizon.panahon.locations.integration.LocationsViewRepository
import com.alvindizon.panahon.repo.PanahonRepo
import com.alvindizon.panahon.repo.PanahonRepoImpl
import com.alvindizon.panahon.searchlocation.integration.SearchLocationViewRepository
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

    @Binds
    abstract fun bindSearchLocationViewRepo(repo: SearchLocationViewRepositoryImpl): SearchLocationViewRepository

    @Binds
    abstract fun bindDetailsViewRepo(repo: DetailsViewRepositoryImpl): DetailsViewRepository
}
