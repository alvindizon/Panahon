package com.alvindizon.panahon.common.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.alvindizon.panahon.core.units.Distance
import com.alvindizon.panahon.core.units.Pressure
import com.alvindizon.panahon.core.units.Speed
import com.alvindizon.panahon.core.units.Temperature
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PreferencesManagerImplTest {

    private val dataStore: DataStore<Preferences> = mockk(relaxed = true)

    private lateinit var preferencesManager: PreferencesManager

    @BeforeEach
    fun setUp() {
        preferencesManager = PreferencesManagerImpl(dataStore)
    }

    @Test
    fun `verify that if datastore has no initial temp unit stored, getTemperatureUnit returns celsius`() =
        runTest {
            every { dataStore.data } returns flow { emit(emptyPreferences()) }
            assertEquals(Temperature.Celsius, preferencesManager.getTemperatureUnit().first())
        }

    @Test
    fun `verify that if datastore already has fahrenheit as temp unit stored, getTemperatureUnit returns fahrenheit`() =
        runTest {
            val mockPreferences: Preferences = mockk {
                every { get(any<Preferences.Key<String>>()) } returns Temperature.Fahrenheit.name
            }
            every { dataStore.data } returns flow { emit(mockPreferences) }

            assertEquals(Temperature.Fahrenheit, preferencesManager.getTemperatureUnit().first())
        }

    @Test
    fun `verify that updateData is called when setTemperatureUnit is called`() = runTest {
        coEvery { dataStore.updateData(any()) } returns mockk()

        preferencesManager.setTemperatureUnit(Temperature.Fahrenheit)

        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `verify that if datastore has no initial speed unit stored, getSpeedUnit returns meters per sec`() =
        runTest {
            every { dataStore.data } returns flow { emit(emptyPreferences()) }
            assertEquals(Speed.MetersPerSec, preferencesManager.getSpeedUnit().first())
        }

    @Test
    fun `verify that if datastore already has miles per hr as speed unit stored, getSpeedUnit returns miles per hr`() =
        runTest {
            val mockPreferences: Preferences = mockk {
                every { get(any<Preferences.Key<String>>()) } returns Speed.MilesPerHour.name
            }
            every { dataStore.data } returns flow { emit(mockPreferences) }

            assertEquals(Speed.MilesPerHour, preferencesManager.getSpeedUnit().first())
        }

    @Test
    fun `verify that updateData is called when setSpeedUnit is called`() = runTest {
        coEvery { dataStore.updateData(any()) } returns mockk()

        preferencesManager.setSpeedUnit(Speed.MilesPerHour)

        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `verify that if datastore has no initial pressure unit stored, getPressureUnit returns hectopascals`() =
        runTest {
            every { dataStore.data } returns flow { emit(emptyPreferences()) }
            assertEquals(Pressure.HectoPascals, preferencesManager.getPressureUnit().first())
        }

    @Test
    fun `verify that if datastore already has inHg as pressure unit stored, getPressureUnit returns inHg`() =
        runTest {
            val mockPreferences: Preferences = mockk {
                every { get(any<Preferences.Key<String>>()) } returns Pressure.InchOfMercury.name
            }
            every { dataStore.data } returns flow { emit(mockPreferences) }

            assertEquals(Pressure.InchOfMercury, preferencesManager.getPressureUnit().first())
        }

    @Test
    fun `verify that updateData is called when setPressureUnit is called`() = runTest {
        coEvery { dataStore.updateData(any()) } returns mockk()

        preferencesManager.setPressureUnit(Pressure.InchOfMercury)

        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `verify that if datastore has no initial distance unit stored, getDistanceUnit returns meters`() =
        runTest {
            every { dataStore.data } returns flow { emit(emptyPreferences()) }
            assertEquals(Distance.Kilometers, preferencesManager.getDistanceUnit().first())
        }

    @Test
    fun `verify that if datastore already has miles as distance unit stored, getDistanceUnit returns miles`() =
        runTest {
            val mockPreferences: Preferences = mockk {
                every { get(any<Preferences.Key<String>>()) } returns Distance.Miles.name
            }
            every { dataStore.data } returns flow { emit(mockPreferences) }

            assertEquals(Distance.Miles, preferencesManager.getDistanceUnit().first())
        }

    @Test
    fun `verify that updateData is called when setDistanceUnit is called`() = runTest {
        coEvery { dataStore.updateData(any()) } returns mockk()

        preferencesManager.setDistanceUnit(Distance.Miles)

        coVerify { dataStore.updateData(any()) }
    }
}