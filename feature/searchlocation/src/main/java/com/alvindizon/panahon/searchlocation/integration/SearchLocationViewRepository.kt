package com.alvindizon.panahon.searchlocation.integration

import com.alvindizon.panahon.searchlocation.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchLocationViewRepository {

    suspend fun saveLocationToDatabase(name: String, latitude: String, longitude: String)

    fun searchForLocation(query: String): Flow<List<SearchResult>>
}
