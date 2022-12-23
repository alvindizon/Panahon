package com.alvindizon.panahon.details.usecase

import com.alvindizon.panahon.core.units.Distance
import com.alvindizon.panahon.details.data.DetailsViewRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@ViewModelScoped
class FetchDistanceUnitUseCase @Inject constructor(private val repository: DetailsViewRepository) {

    operator fun invoke(): Flow<Distance> = repository.fetchDistanceUnit()
}