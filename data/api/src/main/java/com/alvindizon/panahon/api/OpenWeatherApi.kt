package com.alvindizon.panahon.api

import com.alvindizon.panahon.api.model.DirectGeocodeResponseItem
import com.alvindizon.panahon.api.model.OneCallResponse
import com.alvindizon.panahon.api.model.ReverseGeocodeResponseItem
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    @GET("data/2.5/onecall")
    suspend fun getWeather(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("exclude") exclude: String?,
        @Query("units") units: String?,
        @Query("appId") appId: String
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