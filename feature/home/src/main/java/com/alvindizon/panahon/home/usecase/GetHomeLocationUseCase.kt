package com.alvindizon.panahon.home.usecase

import com.alvindizon.panahon.home.data.HomeRepository
import com.alvindizon.panahon.home.model.CurrentLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * gets home location saved in DB,
 * if no home location is saved, get first item in db,
 * returns null if both are not present
 */
@Singleton
class GetHomeLocationUseCase @Inject constructor(private val repo: HomeRepository){

    suspend fun execute(): CurrentLocation? {
        return withContext(Dispatchers.IO) { repo.getHomeLocation() ?: repo.getFirstLocation() }
    }
}
