package com.alvindizon.panahon.details.usecase

import com.alvindizon.panahon.data.location.LocationRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class SaveHomeLocationUseCase @Inject constructor(private val repo: LocationRepository) {

    suspend fun invoke(name: String, latitude: String, longitude: String) {
        withContext(Dispatchers.IO) {
            // get home location
            val currentLocation = repo.getHomeLocation()
            // if another home location exists and it is not the same as the currently detected location,
            // remove existing one as current home location by updating its entry
            if (currentLocation != null &&
                currentLocation.locationName == name &&
                currentLocation.latitude == latitude &&
                currentLocation.longitude == longitude) {
                repo.updateDbLocation(
                    currentLocation.locationName,
                    currentLocation.latitude,
                    currentLocation.longitude,
                    false
                )
            }
            repo.saveLocationToDatabase(name, latitude, longitude, true)
        }
    }
}
