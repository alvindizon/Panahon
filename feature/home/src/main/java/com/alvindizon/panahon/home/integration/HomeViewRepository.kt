package com.alvindizon.panahon.home.integration

import com.alvindizon.panahon.home.model.CurrentLocation

interface HomeViewRepository {
    suspend fun getHomeLocation(): CurrentLocation?
}
