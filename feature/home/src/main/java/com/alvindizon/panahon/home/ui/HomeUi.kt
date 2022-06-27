package com.alvindizon.panahon.home.ui

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alvindizon.panahon.design.components.LoadingScreen
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.home.R
import com.alvindizon.panahon.home.model.CurrentLocation
import com.alvindizon.panahon.home.viewmodel.HomeScreenUiState
import com.alvindizon.panahon.home.viewmodel.HomeScreenViewModel

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    onLocationFound: (CurrentLocation) -> Unit,
    onSnackbarButtonClick: () -> Unit
) {
    val retryLocationEnableRequest =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                viewModel.fetchCurrentLocation()
            }
        }

    val scaffoldState = rememberScaffoldState()
    val (showSnackbar, setShowSnackbar) = remember { mutableStateOf(false) }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.containsValue(false)) {
                setShowSnackbar(true)
            } else {
                viewModel.isLocationOn(retryLocationEnableRequest)
            }
        }

    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(Unit) {
        viewModel.getHomeLocation()
    }

    val state = viewModel.uiState.collectAsState().value

    Scaffold(scaffoldState = scaffoldState) {
        when (state) {
            is HomeScreenUiState.LocationFound -> {
                // wrapped with LaunchedEffect or else this callback will be called multiple times
                LaunchedEffect(Unit) {
                    onLocationFound.invoke(state.location)
                }
            }
            HomeScreenUiState.CheckPreciseLocationEnabled -> {
                LaunchedEffect(Unit) {
                    viewModel.checkPreciseLocationEnabled()
                }
            }
            HomeScreenUiState.PreciseLocationEnabled, HomeScreenUiState.LocationOn -> {
                LaunchedEffect(Unit) {
                    viewModel.fetchCurrentLocation()
                }
            }
            HomeScreenUiState.Loading -> LoadingScreen()
            HomeScreenUiState.ShowRationale -> LocationRationaleScreen(
                scaffoldState = scaffoldState,
                showSnackbar = showSnackbar,
                setShowSnackbar = setShowSnackbar,
                onEnableLocationButtonClick = { permissionLauncher.launch(permissions) },
                onSnackbarButtonClick = { onSnackbarButtonClick.invoke() }
            )

            is HomeScreenUiState.Error ->
                ErrorAlertDialog(
                    errorMessage = state.message ?: stringResource(
                        id = com.alvindizon.panahon.design.R.string.generic_try_again_msg
                    )
                )
        }
    }


}

@Composable
fun LocationRationaleScreen(
    scaffoldState: ScaffoldState,
    showSnackbar: Boolean,
    setShowSnackbar: (Boolean) -> Unit,
    onEnableLocationButtonClick: () -> Unit,
    onSnackbarButtonClick: () -> Unit
) {
    val snackbarMsg = stringResource(R.string.home_snackbar_msg)
    val snackbarBtnMsg = stringResource(R.string.home_snackbar_btn_txt)
    if (showSnackbar) {
        LaunchedEffect(scaffoldState.snackbarHostState) {
            val result = scaffoldState.snackbarHostState.showSnackbar(
                message = snackbarMsg,
                actionLabel = snackbarBtnMsg
            )
            when (result) {
                SnackbarResult.Dismissed -> setShowSnackbar(false)
                SnackbarResult.ActionPerformed -> {
                    setShowSnackbar(false)
                    onSnackbarButtonClick()
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = stringResource(R.string.home_location_rationale_msg),
            style = MaterialTheme.typography.subtitle2,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            textAlign = TextAlign.Center,
            text = stringResource(R.string.home_location_rationale_msg2),
            style = MaterialTheme.typography.subtitle2,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onEnableLocationButtonClick() }) {
            Text(
                text = stringResource(id = R.string.home_share_location_btn),
                style = MaterialTheme.typography.subtitle2
            )
        }
    }
}

@Preview
@Composable
private fun PermissionNotGrantedScreenPreview() {
    val scaffoldState = rememberScaffoldState()
    val (showSnackbar, setShowSnackbar) = remember { mutableStateOf(false) }
    PanahonTheme {
        LocationRationaleScreen(
            scaffoldState = scaffoldState,
            showSnackbar = showSnackbar,
            setShowSnackbar = setShowSnackbar,
            onEnableLocationButtonClick = {},
            onSnackbarButtonClick = {})
    }
}

@Composable
fun ErrorAlertDialog(errorMessage: String) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = {})
            { Text(text = "OK") }
        },
        dismissButton = {
            TextButton(onClick = {})
            { Text(text = "Cancel") }
        },
        title = { Text(text = "Error") },
        text = { Text(text = errorMessage) }
    )
}
