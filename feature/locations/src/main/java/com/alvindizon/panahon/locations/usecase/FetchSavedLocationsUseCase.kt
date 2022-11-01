package com.alvindizon.panahon.locations.usecase

import com.alvindizon.panahon.locations.integration.LocationsViewRepository
import com.alvindizon.panahon.locations.model.LocationForecast
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FetchSavedLocationsUseCase @Inject constructor(private val repo: LocationsViewRepository) {

    suspend fun execute(): List<LocationForecast> = repo.fetchSavedLocations()
}
