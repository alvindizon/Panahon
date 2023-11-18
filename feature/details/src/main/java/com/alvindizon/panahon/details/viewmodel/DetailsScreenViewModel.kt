package com.alvindizon.panahon.details.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.panahon.design.message.UiMessage
import com.alvindizon.panahon.details.model.DetailedForecast
import com.alvindizon.panahon.details.navigation.DetailsNavigation
import com.alvindizon.panahon.details.usecase.FetchCurrentLocationUseCase
import com.alvindizon.panahon.details.usecase.FetchDetailedForecastUseCase
import com.alvindizon.panahon.details.usecase.GetInitialLocationUseCase
import com.alvindizon.panahon.details.usecase.SaveHomeLocationUseCase
import com.alvindizon.panahon.details.usecase.SaveRecentLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class DetailsScreenUiState(
    val detailedForecast: DetailedForecast? = null,
    val isLoading: Boolean? = null,
    val errorMessage: UiMessage? = null,
    val showRationale: Boolean? = null
)

@HiltViewModel
class DetailsScreenViewModel @Inject constructor(
    private val fetchDetailedForecastUseCase: FetchDetailedForecastUseCase,
    private val getInitialLocationUseCase: GetInitialLocationUseCase,
    private val fetchCurrentLocationUseCase: FetchCurrentLocationUseCase,
    private val saveHomeLocationUseCase: SaveHomeLocationUseCase,
    private val saveRecentLocationUseCase: SaveRecentLocationUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsScreenUiState())
    val uiState: StateFlow<DetailsScreenUiState> = _uiState

    private val locationName: String? by lazy {
        savedStateHandle.get<String>(DetailsNavigation.locationArg)
    }
    private val latitude: String? by lazy {
        savedStateHandle.get<String>(DetailsNavigation.latitudeArg)
    }
    private val longitude: String? by lazy {
        savedStateHandle.get<String>(DetailsNavigation.longitudeArg)
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        handleError(exception)
    }

    init {
        onInitialLoad()
    }

    private fun getHomeLocation() {
        viewModelScope.launch(coroutineExceptionHandler) {
            when (val result = getInitialLocationUseCase()) {

                is GetInitialLocationUseCase.Result.HomeLocationFound -> {
                    // fetch forecast
                    val location = result.currentLocation
                    fetchData(
                        location.locationName,
                        location.latitude,
                        location.longitude
                    )
                }

                is GetInitialLocationUseCase.Result.LocationFound -> {
                    // save location to db and fetch forecast
                    val location = result.currentLocation
                    saveRecentLocationUseCase(
                        location.locationName,
                        location.latitude,
                        location.longitude
                    )
                    fetchData(
                        location.locationName,
                        location.latitude,
                        location.longitude
                    )
                }

                GetInitialLocationUseCase.Result.NoLocationFound -> onFetchCurrentLocation()
                is GetInitialLocationUseCase.Result.Error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = UiMessage(
                                result.error.message ?: result.error.javaClass.name
                            )
                        )
                    }

            }
        }
    }


    fun onInitialLoad() {
        _uiState.update { it.copy(isLoading = true) }
        if (locationName != null && latitude != null && longitude != null) {
            fetchData(locationName!!, latitude!!, longitude!!)
        } else {
            getHomeLocation()
        }
    }

    fun onFetchCurrentLocation() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch(coroutineExceptionHandler) {
            when (val result = fetchCurrentLocationUseCase()) {
                is FetchCurrentLocationUseCase.Result.Error,
                FetchCurrentLocationUseCase.Result.LocationNotFound -> {
                    _uiState.update { it.copy(showRationale = true, isLoading = false) }
                }

                is FetchCurrentLocationUseCase.Result.Success -> {
                    result.currentLocation?.let {
                        saveHomeLocationUseCase.invoke(it.locationName, it.latitude, it.longitude)
                        fetchData(it.locationName, it.latitude, it.longitude)
                    }

                }
            }
        }
    }

    private fun fetchData(locationName: String, latitude: String, longitude: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            runCatching {
                fetchDetailedForecastUseCase(locationName, latitude, longitude)
            }.onSuccess {
                it.collectLatest { detailedForecast ->
                    _uiState.value =
                        DetailsScreenUiState(isLoading = false, detailedForecast = detailedForecast)
                }
            }.onFailure {
                handleError(it)
            }
        }
    }

    private fun handleError(error: Throwable) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = UiMessage(error.message ?: error.javaClass.name)
        )
    }
}
