package com.alvindizon.panahon.locations.data

import com.alvindizon.panahon.locations.model.LocationForecast
import kotlinx.coroutines.flow.Flow

interface LocationsViewRepository {

    fun fetchSavedLocations(): Flow<List<LocationForecast>>

    suspend fun deleteLocation(locationForecast: LocationForecast)
}
