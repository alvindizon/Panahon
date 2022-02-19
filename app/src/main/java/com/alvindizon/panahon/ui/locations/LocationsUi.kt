package com.alvindizon.panahon.ui.locations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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


data class LocationForecast(val name: String, val condition: String, val temperature: String)

@Composable
fun LocationsList(
    locationForecasts: List<LocationForecast>,
    onLocationClick: (LocationForecast) -> Unit
) {
    Column {
        LazyColumn(contentPadding = PaddingValues(top = 8.dp)) {
            items(locationForecasts) { locationForecast ->
                LocationsListItem(locationForecast) { onLocationClick(it) }
            }
        }
    }
}

@Composable
fun LocationsListItem(
    locationForecast: LocationForecast,
    onLocationClick: (LocationForecast) -> Unit
) {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .clickable { onLocationClick(locationForecast) }
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Text(
                    text = locationForecast.name,
                    style = MaterialTheme.typography.h3,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = locationForecast.condition, style = MaterialTheme.typography.h4)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text("${locationForecast.temperature}°C", style = MaterialTheme.typography.h3)
        }
    }
}

@Preview
@Composable
private fun LocationsListItemPreview() {
    PanahonTheme {
        LocationsListItem(LocationForecast("San Pedro", "Clouds", "25")) {

        }
    }
}

@Preview
@Composable
private fun LocationsListPreview() {
    val locationForecasts = listOf(
        LocationForecast("Singapore", "Clouds", "25"),
        LocationForecast("Jakarta", "Clouds", "28"),
        LocationForecast("Nizhny Novgorod", "Clouds", "28"),
        LocationForecast("aaaaaaaaaaaaaaaabbbbbbbb", "Sunny", "-1")
    )
    PanahonTheme {
        LocationsList(locationForecasts = locationForecasts, onLocationClick = {})
    }
}
