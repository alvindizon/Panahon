package com.alvindizon.panahon.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alvindizon.panahon.db.model.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: Location)

    @Query("SELECT * FROM savedLocations")
    fun fetchLocations() : Flow<List<Location>>

    @Query("DELETE FROM savedLocations WHERE name = :name")
    suspend fun delete(name: String)
}