package com.alvindizon.features.settings.usecase

import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.units.Temperature
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SetPreferredTempUnitUseCaseTest {

    private val preferencesManager: PreferencesManager = mockk()

    private lateinit var useCase: SetPreferredTempUnitUseCase

    @BeforeEach
    fun setUp() {
        useCase = SetPreferredTempUnitUseCase(preferencesManager)
    }

    @Test
    fun `verify correct unit is passed to preferences manager when set unit usecase is executed`() = runTest {
        coEvery { preferencesManager.setTemperatureUnit(any()) } just Runs

        useCase.execute(1)

        coVerify { preferencesManager.setTemperatureUnit(Temperature.Fahrenheit) }

        useCase.execute(0)

        coVerify { preferencesManager.setTemperatureUnit(Temperature.Celsius) }
    }
}