package com.alvindizon.panahon.integration

import com.alvindizon.panahon.locations.integration.LocationsViewRepository
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.repo.PanahonRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class LocationsViewRepositoryImpl @Inject constructor(private val panahonRepo: PanahonRepo): LocationsViewRepository {

    override fun fetchSavedLocations(): Flow<List<LocationForecast>> {
        return panahonRepo.fetchSavedLocations().map { locations ->
            locations.map {
                val resp = panahonRepo.getWeatherForLocation(it.latitude, it.longitude)
                LocationForecast(
                    it.name,
                    it.latitude,
                    it.longitude,
                    resp.current.weather[0].main,
                    resp.current.temp.roundToInt().toString(),
                    resp.current.weather[0].icon
                )
            }
        }
    }
}
