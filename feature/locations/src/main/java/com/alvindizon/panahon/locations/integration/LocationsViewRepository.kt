package com.alvindizon.panahon.locations.integration

import com.alvindizon.panahon.locations.model.LocationForecast
import kotlinx.coroutines.flow.Flow

interface LocationsViewRepository {

    suspend fun fetchSavedLocations(): List<LocationForecast>

    suspend fun deleteLocation(locationForecast: LocationForecast)
}
