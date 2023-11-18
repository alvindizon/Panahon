package com.alvindizon.panahon.details.usecase

import com.alvindizon.panahon.data.location.LocationRepository
import com.alvindizon.panahon.data.location.model.CurrentLocation
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class FetchCurrentLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    sealed interface Result {
        data class Success(val currentLocation: CurrentLocation?) : Result
        data object LocationNotFound : Result
        data class Error(val error: Throwable) : Result
    }

    suspend operator fun invoke(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val result = locationRepository.getCurrentLocation()
                if (result?.isLocationEnabled == true) {
                    Result.Success(currentLocation = result.currentLocation)
                } else {
                    Result.LocationNotFound
                }
            } catch (e: Exception) {
                Result.Error(error = e)
            }
        }
    }

}