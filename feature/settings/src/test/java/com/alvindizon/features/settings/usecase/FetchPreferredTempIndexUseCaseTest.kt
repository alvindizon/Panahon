package com.alvindizon.features.settings.usecase

import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.units.Temperature
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FetchPreferredTempIndexUseCaseTest {

    private val preferencesManager: PreferencesManager = mockk()

    private lateinit var useCase: FetchPreferredTempIndexUseCase

    @BeforeEach
    fun setUp() {
        useCase = FetchPreferredTempIndexUseCase(preferencesManager)
    }

    @Test
    fun `verify correct ordinal value is returned by usecase given temperature unit returned by preferences`() = runTest {
        coEvery { preferencesManager.getTemperatureUnit() } returns flow { emit(Temperature.Celsius) }

        var result = useCase.execute()

        assertEquals(0, result)

        coEvery { preferencesManager.getTemperatureUnit() } returns flow { emit(Temperature.Fahrenheit) }

        result = useCase.execute()

        assertEquals(1, result)
    }
}