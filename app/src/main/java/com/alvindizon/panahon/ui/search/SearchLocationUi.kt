package com.alvindizon.panahon.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alvindizon.panahon.ui.theme.PanahonTheme


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

@Composable
fun SearchScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Search Screen")
    }
}

@Composable
fun SearchResultItem(searchResult: SearchResult, onResultClick: (SearchResult) -> Unit) {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .clickable { onResultClick(searchResult) }
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                text = searchResult.locationName,
                style = MaterialTheme.typography.h5,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = searchResult.stateCountry,
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
private fun SearchResultItemPreview() {
    PanahonTheme {
        SearchResultItem(
            SearchResult(
                locationName = "Tokyo",
                state = null,
                country = "Japan",
                lat = "0",
                lon = ""
            )
        ) {}
    }
}
