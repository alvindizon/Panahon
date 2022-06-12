package com.alvindizon.panahon.details.usecase

import com.alvindizon.panahon.details.integration.DetailsViewRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FetchDetailedForecastUseCase @Inject constructor(private val repo: DetailsViewRepository) {

    suspend fun execute(name: String, latitude: String, longitude: String) =
        repo.fetchDetailedForecast(name, latitude, longitude)
}
