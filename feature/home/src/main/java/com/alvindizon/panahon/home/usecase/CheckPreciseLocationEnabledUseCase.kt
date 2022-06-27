package com.alvindizon.panahon.home.usecase

import com.alvindizon.panahon.location.LocationManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckPreciseLocationEnabledUseCase @Inject constructor(private val locationManager: LocationManager) {

    suspend fun execute(): Boolean = locationManager.isPreciseLocationEnabled()
}
