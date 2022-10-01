package com.alvindizon.panahon.details.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.alvindizon.panahon.design.R
import com.alvindizon.panahon.design.components.LoadingScreen
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.details.model.DailyForecast
import com.alvindizon.panahon.details.model.DetailedForecast
import com.alvindizon.panahon.details.model.HourlyForecast
import com.alvindizon.panahon.details.viewmodel.DetailsScreenUiState
import com.alvindizon.panahon.details.viewmodel.DetailsScreenViewModel

@Composable
fun DetailsScreen(
    viewModel: DetailsScreenViewModel,
    location: String,
    latitude: String,
    longitude: String,
    onSettingsIconClick: () -> Unit,
    onNavigationIconClick: () -> Unit
) {
    val context = LocalContext.current
    // need this to prevent infinite loop that happens when using functions
    // ref: https://code.luasoftware.com/tutorials/android/jetpack-compose-load-data-collectasstate-common-mistakes/
    LaunchedEffect(true) {
        viewModel.fetchDetailedForecast(location, latitude, longitude)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = location) },
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClick) {
                        Icon(Icons.Filled.Menu, stringResource(id = R.string.menu))
                    }
                },
                actions = {
                    IconButton(onClick = { onSettingsIconClick() }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (val state = viewModel.uiState.collectAsState().value) {
            is DetailsScreenUiState.Success -> DetailedForecastScreen(
                Modifier.padding(padding),
                state.detailedForecast
            )
            is DetailsScreenUiState.Error -> Toast.makeText(
                context,
                "Error: ${state.message}",
                Toast.LENGTH_SHORT
            ).show()
            else -> LoadingScreen(Modifier.padding(padding))
        }
    }
}

@Composable
fun DetailedForecastScreen(modifier: Modifier = Modifier, detailedForecast: DetailedForecast) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TODO handle empty/null data
        with(detailedForecast) {
            MainDetails(
                icon = icon,
                temperature = currentTemp ?: "",
                condition = condition,
                feelsLikeTemp = feelsLikeTemp ?: ""
            )
            AdditionalDetailsRow(
                sunriseTime = sunriseTime ?: "",
                sunsetTime = sunsetTime ?: "",
                maximumTemp = daily?.get(0)?.maximumTemp ?: "",
                minimumTemp = daily?.get(0)?.minimumTemp ?: ""
            )
            HourlyForecastCard(hourlyForecasts = detailedForecast.hourly ?: emptyList())
            DailyForecastCard(dailyForecasts = detailedForecast.daily ?: emptyList())
        }
    }
}

@Composable
fun MainDetails(icon: String, temperature: String, condition: String, feelsLikeTemp: String) {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = "https://openweathermap.org/img/wn/$icon@2x.png",
                    placeholder = painterResource(
                        id = R.drawable.ic_weather_placeholder
                    )
                ),
                contentDescription = "weather icon",
                modifier = Modifier
                    .size(100.dp)
            )
            Column(
                modifier = Modifier.padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    style = MaterialTheme.typography.h2,
                    text = temperature
                )
                Text(
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h4,
                    text = condition
                )
                Text(
                    style = MaterialTheme.typography.subtitle1,
                    text = "Feels like $feelsLikeTemp",
                )
            }
        }
    }
}

@Composable
fun AdditionalDetailsRow(
    sunriseTime: String,
    sunsetTime: String,
    maximumTemp: String,
    minimumTemp: String
) {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "Sunrise: $sunriseTime", style = MaterialTheme.typography.h6)
                Text(text = "Sunset: $sunsetTime", style = MaterialTheme.typography.h6)
            }
            Column(
                modifier = Modifier.padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "High: $maximumTemp", style = MaterialTheme.typography.h6)
                Text(text = "Low: $minimumTemp", style = MaterialTheme.typography.h6)
            }
        }
    }
}

@Composable
fun HourlyForecastItem(time: String, icon: String, temperature: String) {
    Column(
        modifier = Modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = time, style = MaterialTheme.typography.h6)
        Image(
            painter = rememberAsyncImagePainter(
                model = "https://openweathermap.org/img/wn/$icon@2x.png",
                placeholder = painterResource(
                    id = R.drawable.ic_weather_placeholder
                )
            ),
            contentDescription = "weather icon",
            modifier = Modifier
                .size(64.dp)
        )
        Text(text = "$temperature", style = MaterialTheme.typography.h6)

    }
}

@Composable
fun HourlyForecastList(
    hourlyForecasts: List<HourlyForecast>
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        LazyRow(contentPadding = PaddingValues(top = 8.dp)) {
            items(hourlyForecasts) { hourlyForecast ->
                // TODO create ui for null situations
                HourlyForecastItem(
                    hourlyForecast.time ?: "",
                    hourlyForecast.icon ?: "",
                    hourlyForecast.temperature ?: ""
                )
            }
        }
    }
}

@Composable
fun HourlyForecastCard(
    hourlyForecasts: List<HourlyForecast>
) {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = "Next 24 hours",
                style = MaterialTheme.typography.h5
            )
            HourlyForecastList(hourlyForecasts = hourlyForecasts)
        }
    }
}

@Composable
fun DailyForecastItem(time: String, maximumTemp: String, minimumTemp: String, icon: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$maximumTemp/$minimumTemp",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
        Image(
            painter = rememberAsyncImagePainter(
                model = "https://openweathermap.org/img/wn/$icon@2x.png",
                placeholder = painterResource(
                    id = R.drawable.ic_weather_placeholder
                )
            ),
            contentDescription = "weather icon",
            modifier = Modifier
                .size(48.dp)
        )
    }
}

@Composable
fun DailyForecastList(
    dailyForecasts: List<DailyForecast>
) {
    Column(modifier = Modifier.wrapContentHeight()) {
        // TODO create ui for null situations
        dailyForecasts.forEach {
            DailyForecastItem(
                time = it.date ?: "",
                maximumTemp = it.maximumTemp ?: "",
                minimumTemp = it.minimumTemp ?: "",
                icon = it.icon ?: ""
            )
        }
    }
}

@Composable
fun DailyForecastCard(
    dailyForecasts: List<DailyForecast>
) {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = "1 Week Forecast",
                style = MaterialTheme.typography.h5
            )
            DailyForecastList(dailyForecasts = dailyForecasts)
        }
    }
}

@Preview
@Composable
private fun MainDetailsPreview() {
    PanahonTheme {
        MainDetails("04d", "30", "Sunny", "38")

    }
}

@Preview
@Composable
private fun AdditionalDetailsPreview() {
    PanahonTheme {
        AdditionalDetailsRow("5:30 am", "6:00 pm", "30", "24")

    }
}

@Preview
@Composable
private fun HourlyForecastCardPreview() {
    val hourlyForecasts = listOf(
        HourlyForecast("5pm", "32", "01d"),
        HourlyForecast("6pm", "32", "01d"),
        HourlyForecast("7pm", "32", "01d"),
        HourlyForecast("8pm", "32", "01d"),
        HourlyForecast("9pm", "32", "01d"),
    )
    PanahonTheme {
        HourlyForecastCard(hourlyForecasts = hourlyForecasts)
    }
}

@Preview
@Composable
private fun DailyForecastCardPreview() {
    val dailyForecasts = listOf(
        DailyForecast("Wed Jun 08", "30", "28", "Sunny", "01d"),
        DailyForecast("Thu Jun 09", "30", "28", "Sunny", "01d"),
        DailyForecast("Fri Jun 10", "30", "28", "Sunny", "01d"),
        DailyForecast("Sat Jun 11", "30", "28", "Sunny", "01d"),
        DailyForecast("Sun Jun 12", "30", "28", "Sunny", "01d"),
    )
    PanahonTheme {
        DailyForecastCard(dailyForecasts = dailyForecasts)
    }

}

