package com.alvindizon.panahon.home.usecase

import com.alvindizon.panahon.home.data.HomeRepository
import com.alvindizon.panahon.home.model.CurrentLocation
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetHomeLocationUseCaseTest {

    private val repository: HomeRepository = mockk()

    private lateinit var useCase: GetHomeLocationUseCase

    private val expectedLocation = CurrentLocation("Sydney", "-33.865143", "151.2099")

    private val firstLocation = CurrentLocation("Penrith", "-33.751", "150.692")

    @BeforeEach
    fun setUp() {
        useCase = GetHomeLocationUseCase(repository)
    }

    @Test
    fun `when repo returns null and first location returns successfully then usecase should return first location`() = runTest {
        coEvery { repository.getHomeLocation() } returns null
        coEvery { repository.getFirstLocation() } returns firstLocation
        val result = useCase.execute()
        assertEquals(firstLocation, result)
    }

    @Test
    fun `when repo returns location then usecase should return Location`() = runTest {
        coEvery { repository.getHomeLocation() } returns expectedLocation
        val result = useCase.execute()
        assertEquals(expectedLocation, result)
    }

    @Test
    fun `when both home and first locations are null return null`() = runTest {
        coEvery { repository.getHomeLocation() } returns null
        coEvery { repository.getFirstLocation() } returns null
        val result = useCase.execute()
        assertNull(result)
    }
}
