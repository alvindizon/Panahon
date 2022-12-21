package com.alvindizon.panahon.details.ui

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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.alvindizon.panahon.details.R
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.details.model.DailyForecast
import com.alvindizon.panahon.details.model.DetailedForecast
import com.alvindizon.panahon.details.model.HourlyForecast
import com.alvindizon.panahon.details.viewmodel.DetailsScreenUiState
import com.alvindizon.panahon.details.viewmodel.DetailsScreenViewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@Composable
fun DetailsScreen(
    viewModel: DetailsScreenViewModel,
    location: String,
    onSettingsIconClick: () -> Unit,
    onNavigationIconClick: () -> Unit
) {
    val state = viewModel.uiState.collectAsState().value
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = location) },
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClick) {
                        Icon(
                            Icons.Filled.Menu,
                            stringResource(id = com.alvindizon.panahon.design.R.string.menu)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onSettingsIconClick() }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(id = com.alvindizon.panahon.design.R.string.settings)
                        )
                    }
                }
            )
        }
    ) { padding ->
        val refreshState = rememberPullRefreshState(
            refreshing = state.isLoading,
            onRefresh = { viewModel.fetchData() },
        )
        Box(
            modifier = Modifier
                .pullRefresh(refreshState)
                .fillMaxSize() // fillMaxSize so that pull refresh indicator won't be aligned at top start
        ) {
            DetailedForecastScreen(modifier = Modifier.padding(padding), state = state)
            PullRefreshIndicator(
                state.isLoading,
                refreshState,
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(padding)

            )
        }
    }
}

@Composable
internal fun DetailedForecastScreen(
    modifier: Modifier = Modifier,
    state: DetailsScreenUiState
) {
    val scaffoldState = rememberScaffoldState()
    var showSnackBar by remember { mutableStateOf(false) }
    val genericErrorMsg =
        stringResource(id = com.alvindizon.panahon.design.R.string.generic_error_msg)
    if (showSnackBar) {
        LaunchedEffect(scaffoldState.snackbarHostState) {
            val result = scaffoldState.snackbarHostState.showSnackbar(
                message = state.errorMessage ?: genericErrorMsg
            )
            when (result) {
                SnackbarResult.Dismissed, SnackbarResult.ActionPerformed -> showSnackBar = false
            }
        }
    }
    when {
        state.errorMessage != null -> showSnackBar = true
        state.detailedForecast != null -> DetailedForecastScreen(
            modifier = modifier,
            detailedForecast = state.detailedForecast
        )
    }
}

@Composable
fun DetailedForecastScreen(
    modifier: Modifier = Modifier,
    detailedForecast: DetailedForecast
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TODO handle empty/null data
        with(detailedForecast) {
            Text(
                modifier = Modifier.wrapContentWidth(),
                style = MaterialTheme.typography.caption,
                text = stringResource(
                    com.alvindizon.panahon.details.R.string.last_updated,
                    lastUpdatedTime
                ),
            )
            MainDetails(
                icon = icon,
                temperature = currentTemp ?: "",
                condition = condition,
                feelsLikeTemp = feelsLikeTemp ?: ""
            )
            AdditionalDetailsPager(
                sunriseTime = stringResource(R.string.sunrise_label, sunriseTime ?: "N/A"),
                sunsetTime = stringResource(R.string.sunset_label, sunsetTime ?: "N/A"),
                maximumTemp = stringResource(
                    R.string.high_temp_label,
                    daily?.get(0)?.maximumTemp ?: "N/A"
                ),
                minimumTemp = stringResource(
                    R.string.low_temp_label,
                    daily?.get(0)?.minimumTemp ?: "N/A"
                ),
                windSpeed = stringResource(R.string.wind_label, windSpeed),
                pressure = stringResource(R.string.pressure_label, pressure),
                visibility = stringResource(id = R.string.visibility_label, visibility),
                uvIndex = stringResource(id = R.string.uvi_label, uvIndex),
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
                        id = com.alvindizon.panahon.design.R.drawable.ic_weather_placeholder
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
fun AdditionalDetailsCard(
    text1: String,
    text2: String,
    text3: String,
    text4: String
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
                Text(text = text1, style = MaterialTheme.typography.subtitle2)
                Text(text = text2, style = MaterialTheme.typography.subtitle2)
            }
            Column(
                modifier = Modifier.padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = text3, style = MaterialTheme.typography.subtitle2)
                Text(text = text4, style = MaterialTheme.typography.subtitle2)
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
                    id = com.alvindizon.panahon.design.R.drawable.ic_weather_placeholder
                )
            ),
            contentDescription = "weather icon",
            modifier = Modifier
                .size(64.dp)
        )
        Text(text = temperature, style = MaterialTheme.typography.h6)

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
                    id = com.alvindizon.panahon.design.R.drawable.ic_weather_placeholder
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

@Composable
fun AdditionalDetailsPager(
    modifier: Modifier = Modifier,
    sunriseTime: String,
    sunsetTime: String,
    maximumTemp: String,
    minimumTemp: String,
    windSpeed: String,
    pressure: String,
    visibility: String,
    uvIndex: String
) {
    val pagerState = rememberPagerState()

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(count = 2, modifier = modifier, state = pagerState) { page ->
            when (page) {
                0 -> {
                    AdditionalDetailsCard(
                        text1 = sunriseTime,
                        text2 = sunsetTime,
                        text3 = maximumTemp,
                        text4 = minimumTemp
                    )
                }
                1 -> {
                    AdditionalDetailsCard(
                        text1 = windSpeed,
                        text2 = pressure,
                        text3 = visibility,
                        text4 = uvIndex
                    )
                }
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsScreenPreview() {
    val hourlyForecasts = listOf(
        HourlyForecast("5pm", "32°C", "01d"),
        HourlyForecast("6pm", "32°C", "01d"),
        HourlyForecast("7pm", "32°C", "01d"),
        HourlyForecast("8pm", "32°C", "01d"),
        HourlyForecast("9pm", "32°C", "01d"),
    )
    val dailyForecasts = listOf(
        DailyForecast("Wed Jun 08", "30°C", "28°C", "Sunny", "01d"),
        DailyForecast("Thu Jun 09", "30°C", "28°C", "Sunny", "01d"),
        DailyForecast("Fri Jun 10", "30°C", "28°C", "Sunny", "01d"),
        DailyForecast("Sat Jun 11", "30°C", "28°C", "Sunny", "01d"),
        DailyForecast("Sun Jun 12", "30°C", "28°C", "Sunny", "01d"),
    )
    val detailedForecast = DetailedForecast(
        locationName = "San Pedro",
        sunriseTime = "5:30 am",
        sunsetTime = "6:00 pm",
        currentTemp = "30°C",
        feelsLikeTemp = "38°C",
        condition = "Sunny",
        icon = "04d",
        hourly = hourlyForecasts,
        daily = dailyForecasts,
        lastUpdatedTime = "2022-12-21T11:27:00:00+02:00",
        windSpeed = "2 m/s",
        pressure = "1000 hPa",
        visibility = "10 km",
        uvIndex = "0.0",
    )
    PanahonTheme {
        DetailedForecastScreen(detailedForecast = detailedForecast)
    }
}


