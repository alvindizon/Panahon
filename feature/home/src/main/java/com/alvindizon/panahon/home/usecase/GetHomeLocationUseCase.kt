package com.alvindizon.panahon.home.usecase

import com.alvindizon.panahon.home.integration.HomeViewRepository
import com.alvindizon.panahon.home.model.CurrentLocation
import javax.inject.Inject
import javax.inject.Singleton

// gets home location saved in DB, returns null if not present
@Singleton
class GetHomeLocationUseCase @Inject constructor(private val repo: HomeViewRepository){

    suspend fun execute(): CurrentLocation? = repo.getHomeLocation()
}
