package com.alvindizon.panahon.searchlocation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.alvindizon.panahon.searchlocation.integration.SearchLocationViewRepository
import com.alvindizon.panahon.searchlocation.model.SearchResult
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SearchLocationViewModelTest {

    private lateinit var viewModel: SearchLocationViewModel

    private val repo: SearchLocationViewRepository = mockk()

    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = SearchLocationViewModel(repo, savedStateHandle)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `verify ui state contains results when repo emits search results`() = runTest {
        val results = listOf(
            SearchResult(
                "Sydney",
                "NSW",
                "AU",
                "-33.865143",
                "151.2099"
            )
        )
        every { repo.searchForLocation(any()) } returns flowOf(results)

        viewModel.searchForLocations("Sydney")

        advanceUntilIdle()

        assertEquals(results, viewModel.uiState.value.searchResults)
    }

    @Test
    fun `verify ui state contains error message when repo throws Exception`() = runTest {
        val errorMsg = "error!"

        every { repo.searchForLocation(any()) } throws Throwable(errorMsg)

        viewModel.searchForLocations("Sydney")

        advanceUntilIdle()

        assertEquals(errorMsg, viewModel.uiState.value.errorMessage?.message)
    }
}