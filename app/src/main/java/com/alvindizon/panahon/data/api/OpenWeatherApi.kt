package com.alvindizon.panahon.data.api

import com.alvindizon.panahon.BuildConfig
import com.alvindizon.panahon.data.api.model.DirectGeocodeResponseItem
import com.alvindizon.panahon.data.api.model.OneCallResponse
import com.alvindizon.panahon.data.api.model.ReverseGeocodeResponseItem
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    @GET("data/2.5/onecall?appId=${BuildConfig.OPENWEATHER_KEY}")
    suspend fun getWeather(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("exclude") exclude: String?,
        @Query("units") units: String?
    ): OneCallResponse

    @GET("geo/1.0/direct?appId=${BuildConfig.OPENWEATHER_KEY}")
    suspend fun getCities(
        @Query("q") query: String,
        @Query("limit") limit: String
    ): List<DirectGeocodeResponseItem>

    @GET("geo/1.0/reverse?appId=${BuildConfig.OPENWEATHER_KEY}")
    suspend fun getLocationName(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String
    ): List<ReverseGeocodeResponseItem>
}
