package com.alvindizon.panahon.di

import com.alvindizon.panahon.data.OpenWeatherRepo
import com.alvindizon.panahon.data.OpenWeatherRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindOpenWeatherRepo(repo: OpenWeatherRepoImpl): OpenWeatherRepo
}
