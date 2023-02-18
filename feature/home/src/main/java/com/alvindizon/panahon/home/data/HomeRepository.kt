package com.alvindizon.panahon.home.data

import com.alvindizon.panahon.db.LocationDao
import com.alvindizon.panahon.db.model.Location
import com.alvindizon.panahon.home.model.CurrentLocation
import javax.inject.Inject
import javax.inject.Singleton

interface HomeRepository {
    suspend fun getHomeLocation(): CurrentLocation?
    suspend fun saveHomeLocationToDatabase(name: String, latitude: String, longitude: String)
    suspend fun updateDbLocation(name: String, latitude: String, longitude: String, isHomeLocation: Boolean)
}

@Singleton
class HomeRepositoryImpl @Inject constructor(private val dao: LocationDao) :
    HomeRepository {

    override suspend fun getHomeLocation(): CurrentLocation? {
        return dao.getHomeLocation()?.run { CurrentLocation(name, latitude, longitude) }
    }

    override suspend fun saveHomeLocationToDatabase(name: String, latitude: String, longitude: String) =
        dao.insert(Location(name, latitude, longitude, true))

    override suspend fun updateDbLocation(
        name: String,
        latitude: String,
        longitude: String,
        isHomeLocation: Boolean
    ) = dao.update(Location(name, latitude, longitude, isHomeLocation))
}
