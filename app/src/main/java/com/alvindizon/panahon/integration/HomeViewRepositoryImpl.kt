package com.alvindizon.panahon.integration

import com.alvindizon.panahon.home.integration.HomeViewRepository
import com.alvindizon.panahon.home.model.CurrentLocation
import com.alvindizon.panahon.repo.PanahonRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeViewRepositoryImpl @Inject constructor(private val panahonRepo: PanahonRepo) :
    HomeViewRepository {

    override suspend fun getHomeLocation(): CurrentLocation? {
        return panahonRepo.getHomeLocation()?.run { CurrentLocation(name, latitude, longitude) }
    }

    override suspend fun saveHomeLocationToDatabase(name: String, latitude: String, longitude: String) =
        panahonRepo.saveLocationToDatabase(name, latitude, longitude, true)

    override suspend fun updateDbLocation(
        name: String,
        latitude: String,
        longitude: String,
        isHomeLocation: Boolean
    ) = panahonRepo.updateLocation(name, latitude, longitude, isHomeLocation)
}
