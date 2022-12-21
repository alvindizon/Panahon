package com.alvindizon.panahon.details.usecase

import com.alvindizon.panahon.core.units.Distance
import com.alvindizon.panahon.core.units.Pressure
import com.alvindizon.panahon.core.units.Speed
import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.details.data.DetailsViewRepository
import com.alvindizon.panahon.details.model.DetailedForecast
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@ViewModelScoped
class FetchDetailedForecastUseCase @Inject constructor(private val repo: DetailsViewRepository) {

    // TODO remove defaults once combining flows have been figured out
    operator fun invoke(
        name: String,
        latitude: String,
        longitude: String,
        tempUnit: Temperature,
        speed: Speed = Speed.KilometersPerHour,
        pressure: Pressure = Pressure.HectoPascals,
        distance: Distance = Distance.Kilometers
    ): Flow<DetailedForecast> =
        flow { emit(repo.fetchDetailedForecast(name, latitude, longitude, tempUnit, speed, pressure, distance)) }
}
