package com.alvindizon.panahon.common.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
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
}