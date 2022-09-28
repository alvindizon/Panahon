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
    LaunchedEffect(Unit) {
        viewModel.fetchPreferredTemperatureUnit()
    }
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
    ) {
        Settings(state = state, viewModel = viewModel)
    }
}

@Composable
internal fun Settings(state: SettingsUiState, viewModel: SettingsViewModel) {
    val scaffoldState = rememberScaffoldState()
    var showSnackBar by remember { mutableStateOf(false) }
    val initialTempUnitIndex = state.preferredTempUnitIndex
    val genericErrorMsg = stringResource(id = com.alvindizon.panahon.design.R.string.generic_error_msg)
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
        state.isLoading -> LoadingScreen()
        state.errorMessage != null -> showSnackBar = true
        else -> Settings(
            initialTempUnitIndex = initialTempUnitIndex,
            onTempUnitClick = viewModel::setPreferredTemperatureUnit
        )
    }
}

@Composable
internal fun Settings(initialTempUnitIndex: Int, onTempUnitClick: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
            text = stringResource(id = R.string.unit_header)
        )
        MeasurementUnitsCard(
            initialTempUnitIndex = initialTempUnitIndex,
            onTempUnitClick = onTempUnitClick
        )
    }
}

@Composable
internal fun MeasurementUnitsCard(initialTempUnitIndex: Int, onTempUnitClick: (Int) -> Unit) {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 16.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.temperature_label)
            )
            CustomSegmentedControl(
                modifier = Modifier.weight(1f),
                items = Temperature.values().map { it.sign },
                initialSelectedIndex = initialTempUnitIndex,
                onItemClick = onTempUnitClick
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
        ) {
            Settings(initialTempUnitIndex = 0, onTempUnitClick = {})
        }
    }
}

@Preview
@Composable
fun MeasurementUnitsCardPreview() {
    PanahonTheme {
        MeasurementUnitsCard(initialTempUnitIndex = 0, onTempUnitClick = {})
    }
}