package com.alvindizon.panahon.details.usecase

import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.details.data.DetailsViewRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FetchTemperatureUnitUseCaseTest {

    private lateinit var useCase: FetchTemperatureUnitUseCase

    private val repository: DetailsViewRepository = mockk()

    @BeforeEach
    fun setUp() {
        useCase = FetchTemperatureUnitUseCase(repository)
    }

    @Test
    fun `verify correct temperature unit is returned when fetching from repo is successful`() = runTest {
        coEvery { repository.fetchTemperatureUnit() } returns flow { emit(Temperature.Celsius) }

        assertEquals(Temperature.Celsius, useCase().first())
    }

    @Test
    fun `verify that when fetching temperature from repo unit errors, usecase should return error as well`() = runTest {
        val errorMsg = "Error!"
        coEvery { repository.fetchTemperatureUnit() } throws Exception(errorMsg)

        val exception = assertThrows<Exception> { useCase() }
        assertEquals(errorMsg, exception.message)
    }
}