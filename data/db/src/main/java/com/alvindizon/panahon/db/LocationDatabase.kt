package com.alvindizon.panahon.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alvindizon.panahon.db.model.Location

@Database(entities = [Location::class], version = 1)
abstract class LocationDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
}
