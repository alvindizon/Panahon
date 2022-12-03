package com.alvindizon.panahon.details.di

import com.alvindizon.panahon.details.data.DetailsViewRepository
import com.alvindizon.panahon.details.data.DetailsViewRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DetailsRepositoryModule {

    @Binds
    abstract fun bindDetailsViewRepo(repo: DetailsViewRepositoryImpl): DetailsViewRepository
}
