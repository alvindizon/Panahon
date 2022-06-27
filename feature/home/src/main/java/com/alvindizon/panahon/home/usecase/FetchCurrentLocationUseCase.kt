package com.alvindizon.panahon.home.usecase

import com.alvindizon.panahon.home.model.CurrentLocation
import com.alvindizon.panahon.location.LocationManager
import javax.inject.Inject
import javax.inject.Singleton

// fetches location via location manager
@Singleton
class FetchCurrentLocationUseCase @Inject constructor(private val locationManager: LocationManager) {

    suspend fun execute(): CurrentLocation? {
        val location = locationManager.getCurrentLocation()
        var locationName: String? = null
        location?.let {
            locationName = locationManager.getLocationName(it.latitude, it.longitude)
        }
        return if (locationName != null && location != null) {
            CurrentLocation(
                locationName!!,
                location.latitude.toString(),
                location.longitude.toString()
            )
        } else {
            null
        }
    }
}
