package com.alvindizon.panahon.details.usecase

import com.alvindizon.panahon.data.location.LocationRepository
import com.alvindizon.panahon.data.location.model.CurrentLocation
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class GetInitialLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    sealed interface Result {
        data class HomeLocationFound(val currentLocation: CurrentLocation): Result
        data class LocationFound(val currentLocation: CurrentLocation): Result
        data object NoLocationFound: Result
        data class Error(val error: Throwable): Result
    }

    suspend operator fun invoke(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val currentLocation =
                    locationRepository.getHomeLocation() ?:
                    locationRepository.getFirstLocation()

                when (currentLocation?.isHomeLocation) {
                    true -> Result.HomeLocationFound(currentLocation)
                    false -> Result.LocationFound(currentLocation)
                    else -> Result.NoLocationFound
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
}