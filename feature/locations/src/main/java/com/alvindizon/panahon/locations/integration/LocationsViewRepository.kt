package com.alvindizon.panahon.locations.integration

import com.alvindizon.panahon.locations.model.LocationForecast
import kotlinx.coroutines.flow.Flow

interface LocationsViewRepository {

    fun fetchSavedLocations(): Flow<List<LocationForecast>>
}
