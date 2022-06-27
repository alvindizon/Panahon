package com.alvindizon.panahon.home.usecase

import com.alvindizon.panahon.home.integration.HomeViewRepository
import com.alvindizon.panahon.home.model.CurrentLocation
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetHomeLocationUseCaseTest {

    private val repository: HomeViewRepository = mockk()

    private lateinit var useCase: GetHomeLocationUseCase

    private val expectedLocation = CurrentLocation("Sydney", "-33.865143", "151.2099")


    @BeforeEach
    fun setUp() {
        useCase = GetHomeLocationUseCase(repository)
    }

    @Test
    fun `when repo returns null then usecase should return null`() = runTest {
        coEvery { repository.getHomeLocation() } returns null
        val result = useCase.execute()
        assertNull(result)
    }

    @Test
    fun `when repo returns location then usecase should return Location`() = runTest {
        coEvery { repository.getHomeLocation() } returns expectedLocation
        val result = useCase.execute()
        assertEquals(expectedLocation, result)
    }
}
