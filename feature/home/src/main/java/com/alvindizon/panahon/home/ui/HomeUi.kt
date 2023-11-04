package com.alvindizon.panahon.home.ui

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alvindizon.panahon.design.components.ErrorAlertDialog
import com.alvindizon.panahon.design.components.LoadingScreen
import com.alvindizon.panahon.design.components.LocationRationaleScreen
import com.alvindizon.panahon.home.model.CurrentLocation
import com.alvindizon.panahon.home.viewmodel.HomeScreenUiState
import com.alvindizon.panahon.home.viewmodel.HomeScreenViewModel

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    onLocationFound: (CurrentLocation) -> Unit,
    onSnackbarButtonClick: () -> Unit,
    onSearchLinkClick: () -> Unit,
    onErrorOkBtnClick: () -> Unit
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

    Scaffold(scaffoldState = scaffoldState) { padding ->
        when (state) {
            is HomeScreenUiState.LocationFound -> {
                LaunchedEffect(Unit) {
                    viewModel.saveLocationToDb(state.location)
                }
            }
            is HomeScreenUiState.HomeLocationExists -> {
                // wrapped with LaunchedEffect or else this callback will be called multiple times
                LaunchedEffect(Unit) {
                    onLocationFound(state.location)
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
            HomeScreenUiState.Loading -> LoadingScreen(modifier = Modifier.padding(padding))
            is HomeScreenUiState.ShowRationale -> LocationRationaleScreen(
                modifier = Modifier.padding(padding),
                scaffoldState = scaffoldState,
                showSnackbar = showSnackbar,
                setShowSnackbar = setShowSnackbar,
                showLocationUnavailableMsg = state.isLocationUnavailable,
                onEnableLocationButtonClick = { permissionLauncher.launch(permissions) },
                onSnackbarButtonClick = { onSnackbarButtonClick.invoke() },
                onSearchLinkClick = { onSearchLinkClick.invoke() }
            )
            is HomeScreenUiState.Error -> {
                val errorMessage = state.message ?: stringResource(
                    id = com.alvindizon.panahon.design.R.string.generic_try_again_msg
                )
                ErrorAlertDialog(errorMessage) {
                    onErrorOkBtnClick.invoke()
                }
            }
        }
    }
}

