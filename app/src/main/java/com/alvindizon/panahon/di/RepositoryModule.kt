package com.alvindizon.panahon.di

import com.alvindizon.panahon.data.PanahonRepo
import com.alvindizon.panahon.data.PanahonRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindPanahonRepo(repo: PanahonRepoImpl): PanahonRepo
}
