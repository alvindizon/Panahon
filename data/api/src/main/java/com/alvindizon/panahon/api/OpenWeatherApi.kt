package com.alvindizon.panahon.api

import com.alvindizon.panahon.api.model.DirectGeocodeResponseItem
import com.alvindizon.panahon.api.model.OneCallResponse
import com.alvindizon.panahon.api.model.ReverseGeocodeResponseItem
import retrofit2.http.GET
import retrofit2.http.Query

private const val UNIT_METRIC = "metric"

interface OpenWeatherApi {

    @GET("data/2.5/onecall")
    suspend fun getWeather(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("exclude") exclude: String? = null,
        @Query("units") units: String? = UNIT_METRIC,
        @Query("appId") appId: String = BuildConfig.OPENWEATHER_KEY
    ): OneCallResponse

    @GET("geo/1.0/direct")
    suspend fun getCities(
        @Query("q") query: String,
        @Query("limit") limit: String,
        @Query("appId") appId: String
    ): List<DirectGeocodeResponseItem>

    @GET("geo/1.0/reverse}")
    suspend fun getLocationName(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appId") appId: String
    ): List<ReverseGeocodeResponseItem>
}
