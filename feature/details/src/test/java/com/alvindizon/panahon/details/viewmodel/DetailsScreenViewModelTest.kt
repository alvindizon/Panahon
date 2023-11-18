package com.alvindizon.panahon.details.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.alvindizon.panahon.details.model.DetailedForecast
import com.alvindizon.panahon.details.usecase.FetchDetailedForecastUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class DetailsScreenViewModelTest {

    private val fetchDetailedForecastUseCase: FetchDetailedForecastUseCase = mockk()

    private val savedStateHandle: SavedStateHandle = mockk()

    private val viewModel: DetailsScreenViewModel by lazy {
        DetailsScreenViewModel(fetchDetailedForecastUseCase, mockk(), mockk(), mockk(), mockk(), savedStateHandle)
    }

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `verify uistate contains correct error message when fetch detailed forecast usecase errors`() =
        runTest {
            every { savedStateHandle.get<String>(any()) } returns ""
            // seems like you only need coEvery for suspending functions--if you use coEvery here
            // you won't get an error
            every { fetchDetailedForecastUseCase(any(), any(), any()) } throws Throwable("bleh")
            assertEquals("bleh", viewModel.uiState.value.errorMessage?.message)
        }


    @Test
    fun `verify uistate contains correct detailed forecast when fetch forecast usecase succeeds`() =
        runTest {
            val mockForecast = mockk<DetailedForecast> {
                every { locationName } returns "Sydney"
            }
            every { savedStateHandle.get<String>(any()) } returns ""
            every {
                fetchDetailedForecastUseCase(
                    any(),
                    any(),
                    any()
                )
            } returns flow { emit(mockForecast) }

            assertEquals("Sydney", viewModel.uiState.value.detailedForecast?.locationName)
        }
}