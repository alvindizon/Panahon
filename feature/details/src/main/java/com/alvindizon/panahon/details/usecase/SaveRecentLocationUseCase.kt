package com.alvindizon.panahon.details.usecase

import com.alvindizon.panahon.data.location.LocationRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class SaveRecentLocationUseCase @Inject constructor(private val repo: LocationRepository) {

    suspend operator fun invoke(name: String, latitude: String, longitude: String) {
        withContext(Dispatchers.IO) {
            repo.saveLocationToDatabase(name, latitude, longitude, false)
        }
    }
}
