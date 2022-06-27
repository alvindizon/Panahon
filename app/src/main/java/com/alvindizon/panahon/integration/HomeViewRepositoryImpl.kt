package com.alvindizon.panahon.integration

import com.alvindizon.panahon.home.integration.HomeViewRepository
import com.alvindizon.panahon.home.model.CurrentLocation
import com.alvindizon.panahon.repo.PanahonRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeViewRepositoryImpl @Inject constructor(private val panahonRepo: PanahonRepo) :
    HomeViewRepository {

    override suspend fun getHomeLocation(): CurrentLocation? {
        // TODO add call to select item in DB for home location
        return null
    }
}
