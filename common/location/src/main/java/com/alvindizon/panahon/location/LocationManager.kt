package com.alvindizon.panahon.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.alvindizon.panahon.core.utils.PermissionUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface LocationManager {

    suspend fun getCurrentLocation(): Location?

    suspend fun isPreciseLocationEnabled(): Boolean

    suspend fun isLocationOn(locationEnableRequest: ActivityResultLauncher<IntentSenderRequest>?): Boolean

    suspend fun getLocationName(latitude: Double, longitude: Double): String?
}

@Singleton
class LocationManagerImpl @Inject constructor(@ApplicationContext private val context: Context) :
    LocationManager {

    private val locationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val settingsClient: SettingsClient by lazy { LocationServices.getSettingsClient(context) }

    private val geocoder: Geocoder by lazy {
        Geocoder(context, Locale.ENGLISH)
    }

    private val locationRequest by lazy {
        LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(INTERVAL_SEC)
            fastestInterval = TimeUnit.SECONDS.toMillis(FASTEST_INTERVAL_SEC)
            maxWaitTime = TimeUnit.MINUTES.toMillis(MAX_WAIT_TIME_MIN)
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? =
        suspendCancellableCoroutine { cont ->
            val cancellationTokenSource = CancellationTokenSource()
            locationProvider.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { cont.resume(it) }
                .addOnCanceledListener { cont.resume(null) }
                .addOnFailureListener { cont.resumeWithException(it) }


            cont.invokeOnCancellation { cancellationTokenSource.cancel() }
        }

    // show rationale if false
    override suspend fun isPreciseLocationEnabled(): Boolean {
        // check if app has been granted coarse and fine location access
        val hasLocationPermission = PermissionUtils.hasSelfPermissions(
            context, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        // then we check if app has google play services + if location is toggled on
        return if (isGooglePlayServicesAvailable(context) && hasLocationPermission) {
            isLocationOn(null)
        } else {
            false
        }
    }

    override suspend fun isLocationOn(locationEnableRequest: ActivityResultLauncher<IntentSenderRequest>?): Boolean =
        suspendCancellableCoroutine { cont ->
            val locationSettingsRequest =
                LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
            settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener { cont.resume(true) }
                .addOnCanceledListener { cont.resume(false) }
                .addOnFailureListener {
                    (it as? ResolvableApiException)?.let { resolvableE ->
                        showLocationServiceDialog(locationEnableRequest, resolvableE.resolution)
                    }
                    cont.resume(false)
                }
        }

    override suspend fun getLocationName(latitude: Double, longitude: Double): String? =
        withContext(Dispatchers.IO) {
            runCatching {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                var locationName: String? = null
                if (!addresses.isNullOrEmpty()) {
                    val fetchedAddress = addresses[0]
                    if (fetchedAddress.maxAddressLineIndex > -1) {
                        locationName = fetchedAddress.locality ?: fetchedAddress.subLocality
                                ?: fetchedAddress.subAdminArea ?: fetchedAddress.adminArea
                    }
                }
                locationName
            }.getOrNull()
        }

    private fun isGooglePlayServicesAvailable(context: Context): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        return apiAvailability.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }

    private fun showLocationServiceDialog(
        retryLocationEnableRequest: ActivityResultLauncher<IntentSenderRequest>?,
        resolution: PendingIntent
    ) {
        try {
            retryLocationEnableRequest?.launch(IntentSenderRequest.Builder(resolution).build())
        } catch (e: ActivityNotFoundException) {
            // ignore error
        }
    }

    companion object {
        private const val INTERVAL_SEC = 60L
        private const val FASTEST_INTERVAL_SEC = 30L
        private const val MAX_WAIT_TIME_MIN = 2L
    }
}
