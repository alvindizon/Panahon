package com.alvindizon.panahon.home.usecase

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.alvindizon.panahon.location.LocationManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckLocationIsOnUseCase @Inject constructor(private val locationManager: LocationManager) {

    suspend fun execute(locationEnableRequest: ActivityResultLauncher<IntentSenderRequest>): Boolean =
        locationManager.isLocationOn(locationEnableRequest)
}
