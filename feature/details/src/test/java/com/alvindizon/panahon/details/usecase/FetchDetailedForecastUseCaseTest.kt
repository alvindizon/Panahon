package com.alvindizon.panahon.details.usecase

import com.alvindizon.panahon.core.units.Distance
import com.alvindizon.panahon.core.units.Pressure
import com.alvindizon.panahon.core.units.Speed
import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.core.utils.DateTimeUtils
import com.alvindizon.panahon.details.fakes.FakeDetailsViewRepository
import com.alvindizon.panahon.details.model.DetailedForecast
import com.alvindizon.panahon.details.model.RawDaily
import com.alvindizon.panahon.details.model.RawDetailedForecast
import com.alvindizon.panahon.details.model.RawHourly
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


class FetchDetailedForecastUseCaseTest {

    private lateinit var useCase: FetchDetailedForecastUseCase

    private val repo = FakeDetailsViewRepository()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        useCase = FetchDetailedForecastUseCase(repo)
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `verify correct forecast values when one of units changes values`() = runTest {
        val forecastValues = mutableListOf<DetailedForecast>()
        val hourly = RawHourly(
            time = 1672095600,
            temperature = 32.12,
            icon = "04d",
            pop = 0.02
        )
        val daily = RawDaily(
            date = 1672110000,
            maximumTemp = 32.12,
            minimumTemp = 24.12,
            condition = "Sunny",
            icon = "04d",
            pop = 0.02
        )
        val rawDetailedForecast = RawDetailedForecast(
            locationName = "location",
            timezone = "Asia/Manila",
            sunriseTime = 1671920239,
            sunsetTime = 1671960834,
            currentTemp = 25.23,
            feelsLikeTemp = 25.23,
            condition = "broken clouds",
            icon = "04d",
            hourly = List(30){ hourly },
            daily = List(8){ daily },
            lastUpdatedTime = 1671958588,
            windSpeed = 5.14,
            pressure = 1010,
            visibility = 10000,
            uvIndex = 0.17
        )
        val job = launch (UnconfinedTestDispatcher()){
            useCase.invoke("location", "lat", "lon").toList(forecastValues)
        }

        repo.emitTempUnit(Temperature.Celsius)
        repo.emitSpeedUnit(Speed.KilometersPerHour)
        repo.emitPressureUnit(Pressure.HectoPascals)
        repo.emitDistanceUnit(Distance.Kilometers)
        repo.emitRawDetails(rawDetailedForecast)

        assertEquals("1010 hPa", forecastValues[0].pressure)
        assertEquals("18.50 km/h", forecastValues[0].windSpeed)
        assertEquals("25°C", forecastValues[0].currentTemp)
        assertEquals("25°C", forecastValues[0].feelsLikeTemp)
        assertEquals("6:17 am", forecastValues[0].sunriseTime)
        assertEquals("5:33 pm", forecastValues[0].sunsetTime)
        assertEquals("10.00 km", forecastValues[0].visibility)
        assertEquals("0.17", forecastValues[0].uvIndex)
        assertEquals("2%", forecastValues[0].daily?.get(0)?.pop)
        assertEquals("04d", forecastValues[0].icon)
        assertEquals(rawDetailedForecast.icon, forecastValues[0].icon)
        assertEquals(rawDetailedForecast.condition, forecastValues[0].condition)
        assertEquals(DateTimeUtils.HOURLY_ITEMS, forecastValues[0].hourly?.size)
        assertEquals(8, forecastValues[0].daily?.size)

        // simulate change from celsius to fahrenheit
        repo.emitTempUnit(Temperature.Fahrenheit)
        // simulate change from hPa to inHg
        repo.emitPressureUnit(Pressure.InchOfMercury)
        assertEquals("77°F", forecastValues[1].currentTemp)
        assertEquals("90°F", forecastValues[1].daily?.get(0)?.maximumTemp)
        assertEquals("29.83 inHg", forecastValues[2].pressure)
        // check that visibility and speed units remain the same
        assertEquals("18.50 km/h", forecastValues[2].windSpeed)
        assertEquals("10.00 km", forecastValues[2].visibility)

        job.cancel()
    }
}

