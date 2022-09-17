package com.alvindizon.panahon.common.preferences.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.common.preferences.PreferencesManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PreferencesManagerImpl.PREFERENCES_NAME)


    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager =
        PreferencesManagerImpl(context.dataStore)
}