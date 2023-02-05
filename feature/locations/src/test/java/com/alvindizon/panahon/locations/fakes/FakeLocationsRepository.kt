package com.alvindizon.panahon.locations.fakes

import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.locations.data.LocationsViewRepository
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.locations.model.RawSimpleForecast
import com.alvindizon.panahon.locations.model.SavedLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last

class FakeLocationsRepository : LocationsViewRepository {

    private val locationsFlow = MutableSharedFlow<List<SavedLocation>>()
    private val simpleForecastFlow = MutableSharedFlow<RawSimpleForecast>()
    private val tempUnitFlow = MutableSharedFlow<Temperature>()
    private val rawForecastList = mutableListOf<RawSimpleForecast>()

    suspend fun emitTempUnit(temperature: Temperature) = tempUnitFlow.emit(temperature)

    suspend fun emitLocations(locations: List<SavedLocation>) = locationsFlow.emit(locations)

    suspend fun emitRawForecast(rawSimpleForecast: RawSimpleForecast) = simpleForecastFlow.emit(rawSimpleForecast)

    override suspend fun fetchSimpleForecast(
        locationName: String,
        latitude: String,
        longitude: String
    ): RawSimpleForecast = simpleForecastFlow.first()

    override fun fetchTemperatureUnit(): Flow<Temperature> = tempUnitFlow

    override fun fetchSavedLocations(): Flow<List<SavedLocation>> = locationsFlow

    override suspend fun deleteLocation(locationForecast: LocationForecast) = Unit
}