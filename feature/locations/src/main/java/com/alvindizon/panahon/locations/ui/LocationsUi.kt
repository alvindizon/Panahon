package com.alvindizon.panahon.locations.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.alvindizon.panahon.design.R
import com.alvindizon.panahon.design.components.LoadingScreen
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.locations.viewmodel.LocationScreenViewModel


@Composable
fun LocationsScreen(
    viewModel: LocationScreenViewModel,
    onLocationClick: (LocationForecast) -> Unit,
    onUpButtonClicked: () -> Unit,
    onSearchIconClick: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val (showSnackbar, setShowSnackbar) = remember { mutableStateOf(false) }
    val state = viewModel.uiState.collectAsState().value

    if (state.errorMessage != null) setShowSnackbar(true)

    // need this to prevent infinite loop that happens when using functions
    // ref: https://code.luasoftware.com/tutorials/android/jetpack-compose-load-data-collectasstate-common-mistakes/
    LaunchedEffect(true) {
        viewModel.fetchForecasts()
    }

    Scaffold(
        scaffoldState = scaffoldState,
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
    ) { padding ->
        LocationsList(
            modifier = Modifier.padding(padding),
            scaffoldState = scaffoldState,
            locationForecasts = state.list,
            showSnackbar = showSnackbar,
            errorMessage = state.errorMessage,
            isLoading = state.isLoading,
            setShowSnackbar = setShowSnackbar,
            onLocationClick = onLocationClick,
            onItemSwipe = viewModel::deleteLocation
        )
    }
}


@Composable
internal fun LocationsList(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState,
    locationForecasts: List<LocationForecast>,
    showSnackbar: Boolean,
    errorMessage: String?,
    isLoading: Boolean,
    setShowSnackbar: (Boolean) -> Unit,
    onLocationClick: (LocationForecast) -> Unit,
    onItemSwipe: (LocationForecast) -> Unit
) {
    val genericErrorMsg =
        stringResource(id = R.string.generic_error_msg)
    if (showSnackbar) {
        LaunchedEffect(scaffoldState.snackbarHostState) {
            val result = scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessage ?: genericErrorMsg
            )
            when (result) {
                SnackbarResult.Dismissed, SnackbarResult.ActionPerformed -> setShowSnackbar(false)
            }
        }
    }
    if (isLoading) {
        LoadingScreen(modifier = modifier)
    } else {
        LocationsList(
            modifier = modifier,
            locationForecasts = locationForecasts,
            onLocationClick = onLocationClick,
            onItemSwipe = onItemSwipe
        )
    }
}

@Composable
fun LocationsList(
    modifier: Modifier = Modifier,
    locationForecasts: List<LocationForecast>,
    onLocationClick: (LocationForecast) -> Unit,
    onItemSwipe: (LocationForecast) -> Unit
) {
    Column(modifier = modifier) {
        LazyColumn(
            contentPadding = PaddingValues(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (locationForecasts.any { it.isHomeLocation }) {
                item {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = stringResource(id = com.alvindizon.panahon.locations.R.string.current_location),
                        style = MaterialTheme.typography.subtitle1
                    )
                }
            }
            items(
                locationForecasts.filter { it.isHomeLocation },
                key = { it.hashCode() }
            )
            { locationForecast ->
                SwipeableItem(
                    modifier = Modifier.animateItemPlacement(),
                    locationForecast = locationForecast,
                    onItemSwipe = onItemSwipe,
                    onLocationClick = onLocationClick
                )
            }
            if (locationForecasts.any { !it.isHomeLocation }) {
                item {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = stringResource(id = com.alvindizon.panahon.locations.R.string.recent_locations),
                        style = MaterialTheme.typography.subtitle1
                    )
                }
            }
            items(
                locationForecasts.filter { !it.isHomeLocation },
                key = { it.hashCode() }) { locationForecast ->
                SwipeableItem(
                    modifier = Modifier.animateItemPlacement(),
                    locationForecast = locationForecast,
                    onItemSwipe = onItemSwipe,
                    onLocationClick = onLocationClick
                )
            }
        }
    }
}

@Composable
fun SwipeableItem(
    modifier: Modifier = Modifier,
    locationForecast: LocationForecast,
    onItemSwipe: (LocationForecast) -> Unit,
    onLocationClick: (LocationForecast) -> Unit
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                onItemSwipe(locationForecast)
            }
            true
        }
    )
    val elevation by animateDpAsState(if (dismissState.dismissDirection != null) 4.dp else 1.dp)
    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
    )
    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        dismissThresholds = { FractionalThreshold(0.25f) },
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = com.alvindizon.panahon.locations.R.string.delete),
                    modifier = Modifier.scale(scale),
                    tint = Color.White
                )
            }
        }, dismissContent = {
            LocationsListItem(
                locationForecast = locationForecast,
                cardElevation = elevation,
                onLocationClick = onLocationClick
            )
        })
}

@Composable
fun LocationsListItem(
    locationForecast: LocationForecast,
    cardElevation: Dp = 1.dp,
    onLocationClick: (LocationForecast) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .clickable { onLocationClick(locationForecast) }
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = cardElevation
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
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
            temperature,
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
        LocationsListItem(
            LocationForecast(
                "San Pedro",
                "",
                "",
                "Clouds",
                "25",
                "01d",
                true
            )
        ) {

        }
    }
}

@Preview
@Composable
private fun LocationsListPreview() {
    val locationForecasts = listOf(
        LocationForecast("Singapore", "", "", "Clouds", "25", "01d", true),
        LocationForecast("Jakarta", "", "", "Clouds", "28", "01d", true),
        LocationForecast("Nizhny Novgorod", "", "", "Clouds", "28", "01d", true),
        LocationForecast(
            "aaaaaaaaaaaaaaaabbbbbbbb",
            "",
            "",
            "Sunny",
            "-1",
            "01d",
            true
        )
    )
    PanahonTheme {
        LocationsList(
            locationForecasts = locationForecasts, onLocationClick = {}, onItemSwipe = {}
        )
    }
}

@Preview
@Composable
private fun WeatherIconAndTemperaturePreview() {
    PanahonTheme {
        WeatherIconAndTemperature(icon = "01d", temperature = "25")
    }
}
