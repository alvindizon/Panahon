package com.alvindizon.panahon.db.di

import android.content.Context
import androidx.room.Room
import com.alvindizon.panahon.db.LocationDao
import com.alvindizon.panahon.db.LocationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LocationDatabase {
        return Room.databaseBuilder(context, LocationDatabase::class.java, "locations.db").build()
    }

    @Provides
    @Singleton
    fun provideDao(database: LocationDatabase): LocationDao = database.locationDao()
}
