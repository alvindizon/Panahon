package com.alvindizon.panahon.searchlocation.usecase

import com.alvindizon.panahon.searchlocation.integration.SearchLocationViewRepository
import com.alvindizon.panahon.searchlocation.model.SearchResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchForLocationsUseCase @Inject constructor(private val repo: SearchLocationViewRepository) {

    fun execute(query: String): Flow<List<SearchResult>> = repo.searchForLocation(query)
}
