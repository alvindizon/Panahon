package com.alvindizon.panahon.searchlocation.data

import com.alvindizon.panahon.searchlocation.model.SearchResult
import com.alvindizon.panahon.searchlocation.model.CurrentLocation
import kotlinx.coroutines.flow.Flow

interface SearchLocationViewRepository {

    suspend fun saveLocationToDatabase(name: String, latitude: String, longitude: String)

    suspend fun searchForLocation(query: String): Flow<List<SearchResult>>

    suspend fun isPreciseLocationEnabled(): Boolean

    suspend fun fetchCurrentLocation(): CurrentLocation?
}
