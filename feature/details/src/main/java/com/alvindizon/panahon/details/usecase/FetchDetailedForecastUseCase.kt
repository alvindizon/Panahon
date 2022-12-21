package com.alvindizon.panahon.details.usecase

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

    operator fun invoke(
        name: String,
        latitude: String,
        longitude: String,
        tempUnit: Temperature
    ): Flow<DetailedForecast> =
        flow { emit(repo.fetchDetailedForecast(name, latitude, longitude, tempUnit)) }
}
