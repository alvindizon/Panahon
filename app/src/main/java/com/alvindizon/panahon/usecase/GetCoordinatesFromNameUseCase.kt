package com.alvindizon.panahon.usecase

import com.alvindizon.panahon.data.api.OpenWeatherApi
import javax.inject.Inject
import javax.inject.Singleton

typealias Coordinates = Pair<String, String>

@Singleton
class GetCoordinatesFromNameUseCase @Inject constructor(private val openWeatherApi: OpenWeatherApi) {

    suspend fun execute(locationName: String): Coordinates =
        openWeatherApi.getCities(locationName, "1").run {
            Coordinates(this[0].lat.toString(), this[0].lon.toString())
        }

}
