package com.alvindizon.panahon.location

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.alvindizon.panahon.core.utils.PermissionUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class LocationManagerImplTest {

    private val context: Context = mockk()

    private val locationProvider: FusedLocationProviderClient = mockk()

    private val settings: SettingsClient = mockk()

    private lateinit var locationManager: LocationManager

    @BeforeEach
    fun setUp() {
        locationManager = LocationManagerImpl(context)
        mockkStatic(LocationServices::class)
        mockkObject(PermissionUtils)
        mockkStatic(GoogleApiAvailability::class)
        mockkConstructor(LocationSettingsRequest.Builder::class)
        mockkConstructor(Geocoder::class)
        every { LocationServices.getFusedLocationProviderClient(context) } returns locationProvider
        every { LocationServices.getSettingsClient(context) } returns settings
    }

    @Test
    fun `when locationProvider getCurrentLocation returns Location successfully, verify locationManager returns correct Location`() =
        runTest {
            val task = mockTask<Location>()
            val expectedResult: Location = mockk {
                every { latitude } returns -33.865143
                every { longitude } returns 151.209900
            }
            every { locationProvider.getCurrentLocation(any<Int>(), any()) } returns task.apply {
                every { addOnSuccessListener(any()) } answers {
                    firstArg<OnSuccessListener<Location>>().onSuccess(expectedResult)
                    this@apply
                }
            }

            val result = locationManager.getCurrentLocation()
            assertNotNull(result)
            assertEquals(expectedResult.latitude, result!!.latitude)
            assertEquals(expectedResult.longitude, result.longitude)
        }

    @Test
    fun `when locationProvider getCurrentLocation is cancelled, verify locationManager returns null`() =
        runTest {
            val task = mockTask<Location>()
            every { locationProvider.getCurrentLocation(any<Int>(), any()) } returns task.apply {
                every { addOnCanceledListener(any()) } answers {
                    firstArg<OnCanceledListener>().onCanceled()
                    this@apply
                }
            }

            val result = locationManager.getCurrentLocation()
            assertNull(result)
        }

    @Test
    fun `when locationProvider getCurrentLocation errors, verify locationManager returns exception`() =
        runTest {
            val task = mockTask<Location>()
            val exception = Exception("error!")
            every { locationProvider.getCurrentLocation(any<Int>(), any()) } returns task.apply {
                every { addOnFailureListener(any()) } answers {
                    firstArg<OnFailureListener>().onFailure(exception)
                    this@apply
                }
            }
            var result: Location? = null
            var resultException: Exception? = null

            try {
                result = locationManager.getCurrentLocation()
            } catch (e: Exception) {
                resultException = e
            }

            assertNull(result)
            assertNotNull(resultException)
            assertEquals(exception.message, resultException?.message)
        }

    @Test
    fun `when app has location permissions and has google play services and has location settings verify locationManager returns true`() = runTest {
        val task = mockTask<LocationSettingsResponse>()

        every { PermissionUtils.hasSelfPermissions(context, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION) } returns true

        val googleApiAvailability = mockk<GoogleApiAvailability>()
        every { googleApiAvailability.isGooglePlayServicesAvailable(context) } returns ConnectionResult.SUCCESS

        every {
            GoogleApiAvailability.getInstance()
        } returns googleApiAvailability

        every { anyConstructed<LocationSettingsRequest.Builder>().addLocationRequest(any()) } returns mockk {
            every { build() } returns mockk()
        }

        every { settings.checkLocationSettings(any()) } returns task.apply {
            every { addOnSuccessListener(any()) } answers {
                firstArg<OnSuccessListener<LocationSettingsResponse>>().onSuccess(mockk())
                this@apply
            }
        }

        assert(locationManager.isPreciseLocationEnabled())
    }

    @ParameterizedTest
    @MethodSource("errorInputs")
    fun `verify locationManager returns false for various permission and Google Play service availability scenarios`(
        hasLocationPermission: Boolean,
        isGooglePlayServiceAvailable: Int
    ) = runTest {
        val task = mockTask<LocationSettingsResponse>()
        val exception = Exception("error!")

        every { PermissionUtils.hasSelfPermissions(context, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION) } returns hasLocationPermission

        val googleApiAvailability = mockk<GoogleApiAvailability>()
        every { googleApiAvailability.isGooglePlayServicesAvailable(context) } returns isGooglePlayServiceAvailable

        every {
            GoogleApiAvailability.getInstance()
        } returns googleApiAvailability

        every { anyConstructed<LocationSettingsRequest.Builder>().addLocationRequest(any()) } returns mockk {
            every { build() } returns mockk()
        }

        every { settings.checkLocationSettings(any()) } returns task.apply {
            every { addOnFailureListener(any()) } answers {
                firstArg<OnFailureListener>().onFailure(exception)
                this@apply
            }
        }

        assertFalse(locationManager.isPreciseLocationEnabled())
    }

    @Test
    fun `when app has location permissions and has google play services but location settings returns cancel verify locationManager returns false`() = runTest {
        val task = mockTask<LocationSettingsResponse>()

        every { PermissionUtils.hasSelfPermissions(context, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION) } returns true

        val googleApiAvailability = mockk<GoogleApiAvailability>()
        every { googleApiAvailability.isGooglePlayServicesAvailable(context) } returns ConnectionResult.SUCCESS

        every {
            GoogleApiAvailability.getInstance()
        } returns googleApiAvailability

        every { anyConstructed<LocationSettingsRequest.Builder>().addLocationRequest(any()) } returns mockk {
            every { build() } returns mockk()
        }

        every { settings.checkLocationSettings(any()) } returns task.apply {
            every { addOnCanceledListener(any()) } answers {
                firstArg<OnCanceledListener>().onCanceled()
                this@apply
            }
        }

        assertFalse(locationManager.isPreciseLocationEnabled())
    }

    @Test
    fun `when check location settings returns true verify locationManager isLocationEnabled returns true`() = runTest {
        val task = mockTask<LocationSettingsResponse>()

        every { anyConstructed<LocationSettingsRequest.Builder>().addLocationRequest(any()) } returns mockk {
            every { build() } returns mockk()
        }

        every { settings.checkLocationSettings(any()) } returns task.apply {
            every { addOnSuccessListener(any()) } answers {
                firstArg<OnSuccessListener<LocationSettingsResponse>>().onSuccess(mockk())
                this@apply
            }
        }

        assert(locationManager.isLocationOn(null))
    }

    @Test
    fun `when check location settings returns resolvableapiexception verify location request is launched if not null`() = runTest {
        val task = mockTask<LocationSettingsResponse>()
        val resolvableApiException = mockk<ResolvableApiException>(relaxed = true)
        val locationEnableRequest = mockk<ActivityResultLauncher<IntentSenderRequest>>(relaxed = true)

        every { anyConstructed<LocationSettingsRequest.Builder>().addLocationRequest(any()) } returns mockk {
            every { build() } returns mockk()
        }

        every { settings.checkLocationSettings(any()) } returns task.apply {
            every { addOnFailureListener(any()) } answers {
                firstArg<OnFailureListener>().onFailure(resolvableApiException)
                this@apply
            }
        }

        assertFalse(locationManager.isLocationOn(locationEnableRequest))
        verify(exactly = 1) { locationEnableRequest.launch(any()) }
    }

    @ParameterizedTest
    @MethodSource("geocoderInputs")
    fun `verify getLocationName returns expected results depending on geocoder request outcomes`(
        expectedLocality: String?,
        expectedSubLocality: String?,
        expectedSubAdminArea: String?,
        expectedAdminArea: String?,
        expectedName: String?
    ) = runTest {
        val expectedAddress = mockk<Address> {
            every { maxAddressLineIndex } returns 2
            every { locality } returns expectedLocality
            every { subLocality } returns expectedSubLocality
            every { subAdminArea } returns expectedSubAdminArea
            every { adminArea } returns expectedAdminArea
        }
        @Suppress("DEPRECATION")
        every { anyConstructed<Geocoder>().getFromLocation(any(), any(), any()) } returns listOf(expectedAddress)

        val result = locationManager.getLocationName(-33.865143, 151.209900)
        assertEquals(expectedName, result)
    }

    @Test
    fun `verify getLocationName returns null if geocoder request returns null`() = runTest {
        @Suppress("DEPRECATION")
        every { anyConstructed<Geocoder>().getFromLocation(any(), any(), any()) } returns null

        val result = locationManager.getLocationName(-33.865143, 151.209900)
        assertNull(result)
    }

    private fun <T> mockTask() = mockk<Task<T>>().apply {
        every { addOnSuccessListener(any()) } returns this
        every { addOnCanceledListener(any()) } returns this
        every { addOnFailureListener(any()) } returns this
    }

    companion object {
        @JvmStatic
        fun errorInputs() = listOf(
            Arguments.of(false, ConnectionResult.SUCCESS),
            Arguments.of(true, ConnectionResult.API_UNAVAILABLE),
            Arguments.of(false, ConnectionResult.API_UNAVAILABLE)
        )

        @JvmStatic
        fun geocoderInputs() = listOf(
            Arguments.of("Sydney", null, null, null, "Sydney"),
            Arguments.of(null, "sublocality", null, null, "sublocality"),
            Arguments.of(null, null, "subadminarea", null, "subadminarea"),
            Arguments.of(null, null, null, "adminarea", "adminarea"),
            Arguments.of(null, null, null, null, null)
        )
    }
}
