package com.alvindizon.panahon.details.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.panahon.details.model.DetailedForecast
import com.alvindizon.panahon.details.navigation.DetailsNavigation
import com.alvindizon.panahon.details.usecase.FetchDetailedForecastUseCase
import com.alvindizon.panahon.details.usecase.FetchTemperatureUnitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


data class DetailsScreenUiState(
    val detailedForecast: DetailedForecast? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class DetailsScreenViewModel @Inject constructor(
    private val fetchDetailedForecastUseCase: FetchDetailedForecastUseCase,
    private val fetchTemperatureUnitUseCase: FetchTemperatureUnitUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsScreenUiState())
    val uiState: StateFlow<DetailsScreenUiState> = _uiState

    private val locationName by lazy {
        savedStateHandle.get<String>(DetailsNavigation.locationArg) ?: ""
    }
    private val latitude by lazy {
        savedStateHandle.get<String>(DetailsNavigation.latitudeArg) ?: ""
    }
    private val longitude by lazy {
        savedStateHandle.get<String>(DetailsNavigation.longitudeArg) ?: ""
    }

    init {
        viewModelScope.launch {
            runCatching {
                fetchTemperatureUnitUseCase().flatMapLatest { temp ->
                    fetchDetailedForecastUseCase(locationName, latitude, longitude, temp)
                }.catch { handleError(it) }
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

    private fun handleError(throwable: Throwable) {
        _uiState.value = DetailsScreenUiState(
            isLoading = false,
            errorMessage = throwable.message ?: throwable.javaClass.name
        )
    }
}
