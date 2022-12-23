package com.alvindizon.features.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alvindizon.features.settings.viewmodel.SettingsUiState
import com.alvindizon.features.settings.viewmodel.SettingsViewModel
import com.alvindizon.panahon.core.units.Distance
import com.alvindizon.panahon.core.units.Pressure
import com.alvindizon.panahon.core.units.Speed
import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.design.components.CustomSegmentedControl
import com.alvindizon.panahon.design.components.LoadingScreen
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.features.settings.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onUpButtonClicked: () -> Unit
) {
    val state = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = com.alvindizon.panahon.design.R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { onUpButtonClicked() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = com.alvindizon.panahon.design.R.string.back)
                        )
                    }
                },
            )
        }
    ) { padding ->
        Settings(state = state, viewModel = viewModel, modifier = Modifier.padding(padding))
    }
}

@Composable
internal fun Settings(
    state: SettingsUiState,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val scaffoldState = rememberScaffoldState()
    var showSnackBar by remember { mutableStateOf(false) }
    val initialTempUnitIndex = state.preferredTempUnitIndex
    val initialSpeedUnitIndex = state.preferredSpeedUnitIndex
    val initialPressureUnitIndex = state.preferredPressureUnitIndex
    val initialDistanceUnitIndex = state.preferredDistanceUnitIndex
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
        state.isLoading -> LoadingScreen(modifier)
        state.errorMessage != null -> showSnackBar = true
        else -> Settings(
            modifier = modifier,
            initialTempUnitIndex = initialTempUnitIndex,
            initialSpeedUnitIndex = initialSpeedUnitIndex,
            initialPressureUnitIndex = initialPressureUnitIndex,
            initialDistanceUnitIndex = initialDistanceUnitIndex,
            onTempUnitClick = viewModel::setPreferredUnit,
            onSpeedUnitClick = viewModel::setPreferredUnit,
            onPressureUnitClick = viewModel::setPreferredUnit,
            onDistanceUnitClick = viewModel::setPreferredUnit
        )
    }
}

@Composable
internal fun Settings(
    modifier: Modifier = Modifier,
    initialTempUnitIndex: Int,
    initialSpeedUnitIndex: Int,
    initialPressureUnitIndex: Int,
    initialDistanceUnitIndex: Int,
    onTempUnitClick: (String) -> Unit,
    onSpeedUnitClick: (String) -> Unit,
    onPressureUnitClick: (String) -> Unit,
    onDistanceUnitClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(id = R.string.unit_header),
            style = MaterialTheme.typography.subtitle1
        )
        MeasurementUnitsCard(
            initialUnitIndex = initialTempUnitIndex,
            label = stringResource(id = R.string.temperature_unit_label),
            units = Temperature.values().map { it.sign },
            onUnitClick = onTempUnitClick
        )
        MeasurementUnitsCard(
            initialUnitIndex = initialSpeedUnitIndex,
            label = stringResource(id = R.string.speed_unit_label),
            units = Speed.values().map { it.sign },
            onUnitClick = onSpeedUnitClick
        )
        MeasurementUnitsCard(
            initialUnitIndex = initialPressureUnitIndex,
            label = stringResource(id = R.string.pressure_unit_label),
            units = Pressure.values().map { it.sign },
            onUnitClick = onPressureUnitClick
        )
        MeasurementUnitsCard(
            initialUnitIndex = initialDistanceUnitIndex,
            label = stringResource(id = R.string.distance_unit_label),
            units = Distance.values().map { it.sign },
            onUnitClick = onDistanceUnitClick
        )
    }
}

@Composable
internal fun MeasurementUnitsCard(
    initialUnitIndex: Int,
    label: String,
    units: List<String>,
    onUnitClick: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = label, modifier = Modifier.weight(1f).align(Alignment.CenterVertically))
            CustomSegmentedControl(
                modifier = Modifier.weight(2f),
                items = units,
                initialSelectedIndex = initialUnitIndex,
                onItemClick = onUnitClick
            )
        }
    }
}

@Preview
@Composable
fun SettingsUiPreview() {
    PanahonTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = com.alvindizon.panahon.design.R.string.settings)) },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(id = com.alvindizon.panahon.design.R.string.back)
                            )
                        }
                    },
                )
            }
        ) { padding ->
            Settings(
                modifier = Modifier.padding(padding),
                initialTempUnitIndex = 0,
                initialSpeedUnitIndex = 0,
                initialPressureUnitIndex = 0,
                initialDistanceUnitIndex = 0,
                onTempUnitClick = {},
                onSpeedUnitClick = {},
                onPressureUnitClick = {},
                onDistanceUnitClick = {}
            )
        }
    }
}

@Preview
@Composable
fun MeasurementUnitsCardPreview() {
    PanahonTheme {
        MeasurementUnitsCard(
            initialUnitIndex = 0,
            "Temperature",
            listOf("m/s", "km/h", "mi/h"),
            onUnitClick = {})
    }
}