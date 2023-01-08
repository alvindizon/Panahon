package com.alvindizon.panahon.searchlocation.usecase

import com.alvindizon.panahon.searchlocation.integration.SearchLocationViewRepository
import com.alvindizon.panahon.searchlocation.model.SearchResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchForLocationsUseCase @Inject constructor(private val repo: SearchLocationViewRepository) {
    // just returning the result within invoke didn't work
    // I had to expose a flow that feeds the UI state flow with search results
    val flow = MutableSharedFlow<List<SearchResult>>()

    suspend operator fun invoke(query: String) {
        if (query.isEmpty()) {
            flow.emit(emptyList())
        } else {
            flow.emit(repo.searchForLocation(query).first())
        }
    }

}
