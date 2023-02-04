package com.alvindizon.panahon.locations.data

import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.locations.model.RawSimpleForecast
import com.alvindizon.panahon.locations.model.SavedLocation
import kotlinx.coroutines.flow.Flow

interface LocationsViewRepository {

    suspend fun fetchSimpleForecast(
        locationName: String,
        latitude: String,
        longitude: String
    ): RawSimpleForecast

    fun fetchTemperatureUnit(): Flow<Temperature>

    fun fetchSavedLocations(): Flow<List<SavedLocation>>

    suspend fun deleteLocation(locationForecast: LocationForecast)
}
