package com.alvindizon.panahon.home.integration

import com.alvindizon.panahon.home.model.CurrentLocation

interface HomeViewRepository {
    suspend fun getHomeLocation(): CurrentLocation?
    suspend fun saveHomeLocationToDatabase(name: String, latitude: String, longitude: String)
    suspend fun updateDbLocation(name: String, latitude: String, longitude: String, isHomeLocation: Boolean)
}
