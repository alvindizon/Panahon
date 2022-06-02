package com.alvindizon.panahon.searchlocation.usecase

import com.alvindizon.panahon.searchlocation.integration.SearchLocationViewRepository
import com.alvindizon.panahon.searchlocation.model.SearchResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveLocationToDbUseCase @Inject constructor(private val repo: SearchLocationViewRepository) {

    suspend fun execute(result: SearchResult) =
        repo.saveLocationToDatabase(result.locationName, result.lat, result.lon)

}
