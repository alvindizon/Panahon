package com.alvindizon.panahon.usecase

import com.alvindizon.panahon.data.PanahonRepo
import com.alvindizon.panahon.ui.locations.LocationForecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class GetForecastForLocationUseCase @Inject constructor(private val panahonRepo: PanahonRepo) {

    suspend fun execute(latitude: String, longitude: String): LocationForecast =
        withContext(Dispatchers.Default) {
            val reverseGeocodeDeferred =
                async { panahonRepo.getLocationNameFromCoordinates(latitude, longitude) }
            val oneCallDeferred =
                async { panahonRepo.getWeatherForLocation(latitude, longitude) }
            val reverseGeocodeResponse = reverseGeocodeDeferred.await()
            val oneCallResponse = oneCallDeferred.await()
            LocationForecast(
                reverseGeocodeResponse[0].name,
                oneCallResponse.current.weather[0].main,
                oneCallResponse.current.temp.roundToInt().toString()
            )
        }
}
