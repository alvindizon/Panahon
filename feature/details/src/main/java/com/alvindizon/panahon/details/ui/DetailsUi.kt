package com.alvindizon.panahon.details.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alvindizon.panahon.design.components.DataUnavailableScreen
import com.alvindizon.panahon.design.components.LocationRationaleScreen2
import com.alvindizon.panahon.design.theme.Hot
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.design.theme.Snow
import com.alvindizon.panahon.design.utils.IconUtils
import com.alvindizon.panahon.details.R
import com.alvindizon.panahon.details.model.DailyForecast
import com.alvindizon.panahon.details.model.DetailedForecast
import com.alvindizon.panahon.details.model.HourlyForecast
import com.alvindizon.panahon.details.viewmodel.DetailsScreenUiState
import com.alvindizon.panahon.details.viewmodel.DetailsScreenViewModel


@Composable
fun DetailsScreen(
    viewModel: DetailsScreenViewModel,
    onSettingsIconClick: () -> Unit,
    onNavigationIconClick: () -> Unit,
    onSnackbarButtonClick: () -> Unit,
    onSearchLinkClick: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state = viewModel.uiState.collectAsState().value
    val scaffoldState = rememberScaffoldState()
    state.errorMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message.message)
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = state.detailedForecast?.locationName.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClick) {
                        Icon(
                            Icons.Filled.Menu,
                            stringResource(id = com.alvindizon.panahon.design.R.string.menu)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::onInitialLoad) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(id = com.alvindizon.panahon.design.R.string.Refresh)
                        )
                    }
                    IconButton(onClick = onSettingsIconClick) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(id = com.alvindizon.panahon.design.R.string.settings)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        DetailedForecastScreen(
            state = state,
            scaffoldState = scaffoldState,
            paddingValues = paddingValues,
            onRefresh = viewModel::onInitialLoad,
            onSnackbarButtonClick = onSnackbarButtonClick,
            onSearchLinkClick = onSearchLinkClick,
            onLocationPermissionGranted = viewModel::onFetchCurrentLocation
        )
    }
}

@Composable
internal fun DetailedForecastScreen(
    state: DetailsScreenUiState,
    scaffoldState: ScaffoldState,
    paddingValues: PaddingValues,
    onRefresh: () -> Unit,
    onSnackbarButtonClick: () -> Unit,
    onSearchLinkClick: () -> Unit,
    onLocationPermissionGranted: () -> Unit
) {
    val refreshState = rememberPullRefreshState(
        refreshing = state.isLoading ?: false,
        onRefresh = onRefresh,
    )
    var showSnackbar by remember { mutableStateOf(false) }
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.containsValue(false)) {
                showSnackbar = true
            } else {
                onLocationPermissionGranted()
            }
        }

    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    Box(
        modifier = Modifier
            .pullRefresh(refreshState)
            .fillMaxSize() // fillMaxSize so that pull refresh indicator won't be aligned at top start
    ) {
        when {
            state.detailedForecast != null -> {
                DetailedForecastScreen(
                    modifier = Modifier.padding(paddingValues),
                    detailedForecast = state.detailedForecast
                )
            }

            state.showRationale == true -> {
                LocationRationaleScreen2(
                    scaffoldState = scaffoldState,
                    showSnackbar = showSnackbar,
                    onSnackbarDismissed = { showSnackbar = false },
                    onEnableLocationButtonClick = { permissionLauncher.launch(permissions) },
                    onSnackbarButtonClick = onSnackbarButtonClick ,
                    onSearchLinkClick = onSearchLinkClick
                )
            }
        }
        PullRefreshIndicator(
            refreshing = state.isLoading ?: false,
            state = refreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(paddingValues)
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
                text = stringResource(R.string.last_updated, lastUpdatedTime),
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
                painter = painterResource(id = IconUtils.getWeatherIconRes(icon)),
                contentDescription = stringResource(R.string.content_description_icon),
                modifier = Modifier
                    .size(100.dp)
            )
            Column(
                modifier = Modifier.padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    style = MaterialTheme.typography.h3,
                    text = temperature
                )
                Text(
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h5,
                    text = condition
                )
                Text(
                    style = MaterialTheme.typography.subtitle1,
                    text = stringResource(R.string.feels_like_label, feelsLikeTemp)
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
fun HourlyForecastItem(time: String, icon: String, temperature: String, pop: String) {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = time, style = MaterialTheme.typography.h6)
        Image(
            painter = painterResource(id = IconUtils.getWeatherIconRes(icon)),
            contentDescription = stringResource(R.string.content_description_icon),
            modifier = Modifier
                .size(64.dp)
                .padding(vertical = 8.dp)
        )
        Text(text = temperature, style = MaterialTheme.typography.h6)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = pop)
            Image(
                painter = painterResource(id = com.alvindizon.panahon.design.R.drawable.ic_drops),
                contentDescription = stringResource(R.string.content_description_pop),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun HourlyForecastList(
    hourlyForecasts: List<HourlyForecast>
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        if (hourlyForecasts.isEmpty()) {
            DataUnavailableScreen()
        } else {
            LazyRow(contentPadding = PaddingValues(top = 8.dp)) {
                items(hourlyForecasts) { hourlyForecast ->
                    // TODO create ui for null situations
                    HourlyForecastItem(
                        hourlyForecast.time ?: "",
                        hourlyForecast.icon ?: "",
                        hourlyForecast.temperature ?: "",
                        hourlyForecast.pop
                    )
                }
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
                text = stringResource(R.string.hourly_forecast_label),
                style = MaterialTheme.typography.h6
            )
            HourlyForecastList(hourlyForecasts = hourlyForecasts)
        }
    }
}

@Composable
fun DailyForecastItem(
    time: String,
    maximumTemp: String,
    minimumTemp: String,
    icon: String,
    pop: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )
        Row(
            modifier = Modifier.weight(2f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = IconUtils.getWeatherIconRes(icon)),
                contentDescription = stringResource(id = R.string.content_description_icon),
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
                    .weight(1f)
            )
            Text(
                text = maximumTemp,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                color = Hot
            )
            Text(
                text = minimumTemp,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                color = Snow
            )
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = pop, style = MaterialTheme.typography.subtitle2)
                Image(
                    painter = painterResource(id = com.alvindizon.panahon.design.R.drawable.ic_drops),
                    contentDescription = stringResource(R.string.content_description_pop),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun DailyForecastList(
    dailyForecasts: List<DailyForecast>
) {
    Column(modifier = Modifier.wrapContentHeight()) {
        if (dailyForecasts.isEmpty()) {
            DataUnavailableScreen()
        } else {
            // TODO create ui for null situations
            dailyForecasts.forEach {
                DailyForecastItem(
                    time = it.date ?: "",
                    maximumTemp = it.maximumTemp ?: "",
                    minimumTemp = it.minimumTemp ?: "",
                    icon = it.icon ?: "",
                    pop = it.pop
                )
            }
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
                text = stringResource(R.string.daily_forecast_label),
                style = MaterialTheme.typography.h6
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
    val pagerState = rememberPagerState { 2 }

    Column(modifier = modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState) { page ->
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
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(2) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsScreenPreview() {
    val hourlyForecasts = listOf(
        HourlyForecast("5pm", "32°C", "01d", "2%"),
        HourlyForecast("6pm", "32°C", "01d", "2%"),
        HourlyForecast("7pm", "32°C", "01d", "2%"),
        HourlyForecast("8pm", "32°C", "01d", "2%"),
        HourlyForecast("10pm", "32°C", "01d", "2%"),
        HourlyForecast("11pm", "32°C", "01d", "2%"),
        HourlyForecast("12pm", "32°C", "01d", "2%")
    )
    val dailyForecasts = listOf(
        DailyForecast("Wed Jun 08", "30°C", "28°C", "Sunny", "01d", "2%"),
        DailyForecast("Thu Jun 09", "30°C", "28°C", "Sunny", "01d", "2%"),
        DailyForecast("Fri Jun 10", "30°C", "28°C", "Sunny", "01d", "2%"),
        DailyForecast("Sat Jun 11", "30°C", "28°C", "Sunny", "01d", "2%"),
        DailyForecast("Mon Jun 12", "30°C", "28°C", "Sunny", "01d", "2%"),
        DailyForecast("Tue Jun 13", "30°C", "28°C", "Sunny", "01d", "2%"),
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


