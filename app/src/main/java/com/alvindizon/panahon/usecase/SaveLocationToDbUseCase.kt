package com.alvindizon.panahon.usecase

import com.alvindizon.panahon.data.PanahonRepo
import com.alvindizon.panahon.data.db.model.Location
import com.alvindizon.panahon.ui.search.SearchResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveLocationToDbUseCase @Inject constructor(private val panahonRepo: PanahonRepo) {

    suspend fun execute(result: SearchResult) =
        panahonRepo.saveLocationToDatabase(Location(result.locationName, result.lat, result.lon))

}
