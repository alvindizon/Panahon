package com.alvindizon.panahon.searchlocation.di

import com.alvindizon.panahon.searchlocation.data.SearchLocationViewRepository
import com.alvindizon.panahon.searchlocation.data.SearchLocationViewRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchLocationModule {

    @Binds
    abstract fun bindSearchLocationRepo(repo: SearchLocationViewRepositoryImpl) :
        SearchLocationViewRepository
}