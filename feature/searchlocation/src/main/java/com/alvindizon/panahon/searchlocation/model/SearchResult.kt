package com.alvindizon.panahon.searchlocation.model

data class SearchResult(
    val locationName: String,
    val state: String?,
    val country: String,
    val lat: String,
    val lon: String
) {
    val stateCountry
        get() = if (state != null) "$state, $country" else country
}
