package com.alvindizon.panahon.usecase

import com.alvindizon.panahon.data.api.OpenWeatherApi
import com.alvindizon.panahon.ui.search.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchForLocationsUseCase @Inject constructor(private val openWeatherApi: OpenWeatherApi) {

    fun execute(locationQuery: String): Flow<List<SearchResult>> =
        flow {
            emit(openWeatherApi.getCities(locationQuery, "5").map {
                SearchResult(it.name, it.state, it.country, it.lat.toString(), it.lon.toString())
            })
        }
}
