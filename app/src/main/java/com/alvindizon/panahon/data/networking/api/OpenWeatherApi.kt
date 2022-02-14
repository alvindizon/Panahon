package com.alvindizon.panahon.data.networking.api

import com.alvindizon.panahon.data.networking.api.model.DirectGeocodeResponse
import com.alvindizon.panahon.data.networking.api.model.OneCallResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    @GET("data/2.5/onecall")
    suspend fun getWeather(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("exclude") exclude: String,
        @Query("units") units: String,
        @Query("appId") appId: String
    ): OneCallResponse

    @GET("geo/1.0/direct")
    suspend fun getCities(
        @Query("q") query: String,
        @Query("limit") limit: String = "5",
        @Query("appId") appId: String
    ): DirectGeocodeResponse
}
