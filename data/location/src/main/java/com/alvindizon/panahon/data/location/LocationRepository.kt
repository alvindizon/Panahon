package com.alvindizon.panahon.data.location

import com.alvindizon.panahon.api.BuildConfig
import com.alvindizon.panahon.api.OpenWeatherApi
import com.alvindizon.panahon.api.model.DirectGeocodeResponseItem
import com.alvindizon.panahon.data.location.model.CurrentLocation
import com.alvindizon.panahon.data.location.model.LocationResult
import com.alvindizon.panahon.data.location.model.SearchResult
import com.alvindizon.panahon.db.LocationDao
import com.alvindizon.panahon.db.model.Location
import com.alvindizon.panahon.location.LocationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

interface LocationRepository {
    suspend fun getHomeLocation(): CurrentLocation?
    suspend fun getCurrentLocation(): LocationResult?
    suspend fun saveLocationToDatabase(
        name: String,
        latitude: String,
        longitude: String,
        isHomeLocation: Boolean
    )

    suspend fun updateDbLocation(
        name: String,
        latitude: String,
        longitude: String,
        isHomeLocation: Boolean
    )

    suspend fun getFirstLocation(): CurrentLocation?

    suspend fun searchForLocation(query: String): Flow<List<SearchResult>>
}

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val dao: LocationDao,
    private val locationManager: LocationManager,
    private val api: OpenWeatherApi,
) :
    LocationRepository {

    override suspend fun getHomeLocation(): CurrentLocation? {
        return dao.getHomeLocation()?.run { CurrentLocation(true, name, latitude, longitude) }
    }

    override suspend fun getCurrentLocation(): LocationResult {
        return if (locationManager.isPreciseLocationEnabled()) {
            val location = locationManager.getCurrentLocation()
            val currentLocation = location?.let {
                locationManager.getLocationName(it.latitude, it.longitude)
            }?.run {
                CurrentLocation(
                    false,
                    this,
                    location.latitude.toString(),
                    location.longitude.toString()
                )
            }
            return LocationResult(
                isLocationEnabled = true,
                currentLocation = currentLocation
            )
        } else {
            LocationResult(isLocationEnabled = false, currentLocation = null)
        }
    }

    override suspend fun saveLocationToDatabase(
        name: String,
        latitude: String,
        longitude: String,
        isHomeLocation: Boolean
    ) =
        dao.insert(Location(name, latitude, longitude, isHomeLocation))

    override suspend fun updateDbLocation(
        name: String,
        latitude: String,
        longitude: String,
        isHomeLocation: Boolean
    ) = dao.update(Location(name, latitude, longitude, isHomeLocation))

    override suspend fun getFirstLocation(): CurrentLocation? =
        dao.getFirstLocation()?.run { CurrentLocation(false, name, latitude, longitude) }

    override suspend fun searchForLocation(query: String): Flow<List<SearchResult>> =
        flowOf(api.getCities(query, RESULT_LIMIT, BuildConfig.OPENWEATHER_KEY).toSearchResults())

    private fun List<DirectGeocodeResponseItem>.toSearchResults(): List<SearchResult> =
        map { SearchResult(it.name, it.state, it.country, it.lat.toString(), it.lon.toString()) }

    companion object {
        private const val RESULT_LIMIT = "5"
    }
}
