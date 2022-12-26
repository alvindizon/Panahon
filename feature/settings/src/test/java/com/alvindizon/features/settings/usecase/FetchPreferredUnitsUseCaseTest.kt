package com.alvindizon.features.settings.usecase

import com.alvindizon.features.settings.fakes.FakePreferenceManager
import com.alvindizon.panahon.core.units.Distance
import com.alvindizon.panahon.core.units.Pressure
import com.alvindizon.panahon.core.units.Speed
import com.alvindizon.panahon.core.units.Temperature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class FetchPreferredUnitsUseCaseTest {

    private val preferencesManager = FakePreferenceManager()

    private lateinit var useCase: FetchPreferredUnitsUseCase

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        useCase = FetchPreferredUnitsUseCase(preferencesManager)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `verify correct ordinal value is returned by usecase given temperature unit returned by preferences`() =
        runTest {
            val observedValues = mutableListOf<List<Int>>()
            val job = launch (UnconfinedTestDispatcher()){
                useCase.invoke().toList(observedValues)
            }

            preferencesManager.emitTempUnit(Temperature.Celsius)
            preferencesManager.emitSpeedUnit(Speed.KilometersPerHour)
            preferencesManager.emitPressureUnit(Pressure.HectoPascals)
            preferencesManager.emitDistanceUnit(Distance.Kilometers)

            assertEquals(listOf(0, 1, 0, 0), observedValues[0])

            // simulate change in preferences choice
            preferencesManager.emitTempUnit(Temperature.Fahrenheit)

            assertEquals(listOf(1, 1, 0, 0), observedValues[1])

            job.cancel()
        }
}