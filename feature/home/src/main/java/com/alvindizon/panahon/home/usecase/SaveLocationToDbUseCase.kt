package com.alvindizon.panahon.home.usecase

import com.alvindizon.panahon.home.data.HomeRepository
import com.alvindizon.panahon.home.model.CurrentLocation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveLocationToDbUseCase @Inject constructor(private val repo: HomeRepository) {

    suspend fun execute(name: String, latitude: String, longitude: String) {
        val locationToBeSaved = CurrentLocation(name, latitude, longitude)
        // get home location
        val currentLocation = repo.getHomeLocation()
        // if another home location exists and it is not the same as the currently detected location,
        // remove existing one as current home location by updating its entry
        if (currentLocation != null && locationToBeSaved != currentLocation) {
            repo.updateDbLocation(currentLocation.locationName, currentLocation.latitude, currentLocation.longitude, false)
        }
        repo.saveHomeLocationToDatabase(name, latitude, longitude)
    }
}
