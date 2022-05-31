package com.alvindizon.panahon.usecase

import com.alvindizon.panahon.repo.PanahonRepo
import com.alvindizon.panahon.ui.locations.LocationForecast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class FetchForecastsUseCase @Inject constructor(private val panahonRepo: PanahonRepo) {

    fun execute(): Flow<List<LocationForecast>> {
        return panahonRepo.fetchSavedLocations().map { locations ->
            locations.map {
                val resp = panahonRepo.getWeatherForLocation(it.latitude, it.longitude)
                LocationForecast(
                    it.name,
                    resp.current.weather[0].main,
                    resp.current.temp.roundToInt().toString(),
                    resp.current.weather[0].icon
                )
            }
        }
    }
}
