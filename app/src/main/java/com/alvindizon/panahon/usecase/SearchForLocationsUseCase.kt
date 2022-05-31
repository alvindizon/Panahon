package com.alvindizon.panahon.usecase

import com.alvindizon.panahon.repo.PanahonRepo
import com.alvindizon.panahon.ui.search.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchForLocationsUseCase @Inject constructor(private val panahonRepo: PanahonRepo) {

    fun execute(locationQuery: String): Flow<List<SearchResult>> =
        flow {
            emit(panahonRepo.getCoordinatesFromLocationName(locationQuery, "5").map {
                SearchResult(it.name, it.state, it.country, it.lat.toString(), it.lon.toString())
            })
        }
}
