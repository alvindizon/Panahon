package com.alvindizon.panahon.locations.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.alvindizon.panahon.design.R
import com.alvindizon.panahon.design.components.LoadingScreen
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.locations.viewmodel.LocationScreenUiState
import com.alvindizon.panahon.locations.viewmodel.LocationScreenViewModel


@Composable
fun LocationsScreen(
    viewModel: LocationScreenViewModel,
    onLocationClick: (LocationForecast) -> Unit,
    onUpButtonClicked: () -> Unit,
    onSearchIconClick: () -> Unit,
) {
    val context = LocalContext.current

    // need this to prevent infinite loop that happens when using functions
    // ref: https://code.luasoftware.com/tutorials/android/jetpack-compose-load-data-collectasstate-common-mistakes/
    LaunchedEffect(true) {
        viewModel.fetchForecasts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = com.alvindizon.panahon.locations.R.string.locations)) },
                navigationIcon = {
                    IconButton(onClick = { onUpButtonClicked() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onSearchIconClick() }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(id = R.string.search)
                        )
                    }
                }
            )
        }
    ) {
        when (val state = viewModel.locationScreenUiState.collectAsState().value) {
            LocationScreenUiState.Loading -> LoadingScreen()
            is LocationScreenUiState.Success -> {
                LocationsList(
                    locationForecasts = state.list,
                    onLocationClick = { onLocationClick(it) }
                )
            }
            is LocationScreenUiState.Error -> {
                Toast.makeText(
                    context,
                    "Error: ${state.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> Text(
                text = "No data available",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

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
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = locationForecast.name,
                    style = MaterialTheme.typography.h5,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = locationForecast.condition, style = MaterialTheme.typography.h5)
            }
            WeatherIconAndTemperature(
                icon = locationForecast.icon,
                temperature = locationForecast.temperature,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun WeatherIconAndTemperature(
    icon: String,
    temperature: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = "https://openweathermap.org/img/wn/$icon@2x.png"),
            contentDescription = "weather icon",
            modifier = Modifier
                .size(72.dp)
                .weight(1f)
        )
        Text(
            "$temperature°C",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun LocationsListItemPreview() {
    PanahonTheme {
        LocationsListItem(LocationForecast("San Pedro", "", "", "Clouds", "25", "01d")) {

        }
    }
}

@Preview
@Composable
private fun LocationsListPreview() {
    val locationForecasts = listOf(
        LocationForecast("Singapore", "", "", "Clouds", "25", "01d"),
        LocationForecast("Jakarta", "", "", "Clouds", "28", "01d"),
        LocationForecast("Nizhny Novgorod", "", "", "Clouds", "28", "01d"),
        LocationForecast("aaaaaaaaaaaaaaaabbbbbbbb", "", "", "Sunny", "-1", "01d")
    )
    PanahonTheme {
        LocationsList(locationForecasts = locationForecasts, onLocationClick = {})
    }
}

@Preview
@Composable
private fun WeatherIconAndTemperaturePreview() {
    PanahonTheme {
        WeatherIconAndTemperature(icon = "01d", temperature = "25")
    }
}
