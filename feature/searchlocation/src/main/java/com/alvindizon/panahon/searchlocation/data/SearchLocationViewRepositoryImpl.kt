package com.alvindizon.panahon.searchlocation.data

import com.alvindizon.panahon.api.BuildConfig
import com.alvindizon.panahon.api.OpenWeatherApi
import com.alvindizon.panahon.api.model.DirectGeocodeResponseItem
import com.alvindizon.panahon.db.LocationDao
import com.alvindizon.panahon.db.model.Location
import com.alvindizon.panahon.searchlocation.model.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchLocationViewRepositoryImpl @Inject constructor(
    private val api: OpenWeatherApi,
    private val dao: LocationDao
) : SearchLocationViewRepository {

    override suspend fun saveLocationToDatabase(name: String, latitude: String, longitude: String) =
        dao.insert(Location(name, latitude, longitude, false))

    override suspend fun searchForLocation(query: String): Flow<List<SearchResult>> =
        flowOf(api.getCities(query, RESULT_LIMIT, BuildConfig.OPENWEATHER_KEY).toSearchResults())

    private fun List<DirectGeocodeResponseItem>.toSearchResults(): List<SearchResult> =
        map { SearchResult(it.name, it.state, it.country, it.lat.toString(), it.lon.toString()) }

    companion object {
        private const val RESULT_LIMIT = "5"
    }
}