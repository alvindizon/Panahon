package com.alvindizon.panahon.searchlocation.usecase

import com.alvindizon.panahon.data.location.LocationRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class SaveLocationUseCase @Inject constructor(private val locationRepository: LocationRepository) {

    suspend operator fun invoke(name: String, latitude: String, longitude: String) {
        withContext(Dispatchers.IO) {
            locationRepository.saveLocationToDatabase(name, latitude, longitude, false)
        }
    }
}