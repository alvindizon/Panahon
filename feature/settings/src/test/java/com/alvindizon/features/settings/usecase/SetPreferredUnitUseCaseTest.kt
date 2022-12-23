package com.alvindizon.features.settings.usecase

import com.alvindizon.panahon.common.preferences.PreferencesManager
import com.alvindizon.panahon.core.units.Distance
import com.alvindizon.panahon.core.units.Pressure
import com.alvindizon.panahon.core.units.Speed
import com.alvindizon.panahon.core.units.Temperature
import io.mockk.*
import kotlinx.coroutines.test.runTest

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SetPreferredUnitUseCaseTest {

    private val preferencesManager: PreferencesManager = mockk()

    private lateinit var useCase: SetPreferredUnitUseCase

    @BeforeEach
    fun setUp() {
        useCase = SetPreferredUnitUseCase(preferencesManager)
    }

    @Test
    fun `verify correct temp unit is passed to preferences manager when set unit usecase is executed`() = runTest {
        coEvery { preferencesManager.setTemperatureUnit(any()) } just Runs

        useCase(Temperature.Celsius.sign)

        coVerify { preferencesManager.setTemperatureUnit(Temperature.Celsius) }
    }

    @Test
    fun `verify correct speed unit is passed to preferences manager when set unit usecase is executed`() = runTest {
        coEvery { preferencesManager.setSpeedUnit(any()) } just Runs

        useCase(Speed.KilometersPerHour.sign)

        coVerify { preferencesManager.setSpeedUnit(Speed.KilometersPerHour) }
    }

    @Test
    fun `verify correct pressure unit is passed to preferences manager when set unit usecase is executed`() = runTest {
        coEvery { preferencesManager.setPressureUnit(any()) } just Runs

        useCase(Pressure.HectoPascals.sign)

        coVerify { preferencesManager.setPressureUnit(Pressure.HectoPascals) }
    }

    @Test
    fun `verify correct distance unit is passed to preferences manager when set unit usecase is executed`() = runTest {
        coEvery { preferencesManager.setDistanceUnit(any()) } just Runs

        useCase(Distance.Miles.sign)

        coVerify { preferencesManager.setDistanceUnit(Distance.Miles) }
    }
}