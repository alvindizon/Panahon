package com.alvindizon.panahon.integration

import com.alvindizon.panahon.repo.PanahonRepo
import com.alvindizon.panahon.searchlocation.integration.SearchLocationViewRepository
import com.alvindizon.panahon.searchlocation.model.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchLocationViewRepositoryImpl @Inject constructor(private val panahonRepo: PanahonRepo): SearchLocationViewRepository {

    override suspend fun saveLocationToDatabase(name: String, latitude: String, longitude: String) =
        panahonRepo.saveLocationToDatabase(name, latitude, longitude, false)

    override fun searchForLocation(query: String): Flow<List<SearchResult>> =
        flow {
            emit(panahonRepo.getCoordinatesFromLocationName(query, RESULT_LIMIT).map {
                SearchResult(it.name, it.state, it.country, it.lat.toString(), it.lon.toString())
            })
        }

    companion object {
        private const val RESULT_LIMIT = "5"
    }
}
