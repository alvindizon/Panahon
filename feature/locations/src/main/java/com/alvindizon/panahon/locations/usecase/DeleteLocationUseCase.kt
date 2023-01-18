package com.alvindizon.panahon.locations.usecase

import com.alvindizon.panahon.locations.data.LocationsViewRepository
import com.alvindizon.panahon.locations.model.LocationForecast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteLocationUseCase @Inject constructor(private val repo: LocationsViewRepository) {

    suspend fun execute(locationForecast: LocationForecast) = repo.deleteLocation(locationForecast)
}
