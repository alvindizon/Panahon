package com.alvindizon.panahon.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alvindizon.panahon.db.model.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: Location)

    @Query("SELECT * FROM savedLocations ORDER BY ROWID DESC")
    fun fetchLocations() : Flow<List<Location>>

    @Query("DELETE FROM savedLocations WHERE name = :name")
    fun delete(name: String)

    @Query("SELECT * FROM savedLocations WHERE isHomeLocation")
    fun getHomeLocation(): Location?

    @Update(entity = Location::class)
    fun update(location: Location)

    @Query("SELECT * FROM savedLocations ORDER BY ROWID DESC LIMIT 1")
    fun getFirstLocation(): Location?
}
