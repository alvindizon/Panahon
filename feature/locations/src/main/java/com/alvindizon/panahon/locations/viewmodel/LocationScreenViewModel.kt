package com.alvindizon.panahon.locations.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.locations.usecase.DeleteLocationUseCase
import com.alvindizon.panahon.locations.usecase.FetchSavedLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class LocationScreenUiState(
    val list: List<LocationForecast> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    companion object {
        val Empty = LocationScreenUiState()
    }
}

@HiltViewModel
class LocationScreenViewModel @Inject constructor(
    private val fetchSavedLocationsUseCase: FetchSavedLocationsUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationScreenUiState.Empty)
    val uiState: StateFlow<LocationScreenUiState> = _uiState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        _uiState.value = LocationScreenUiState(errorMessage = exception.message ?: exception.javaClass.name)
    }

    fun fetchForecasts() {
        _uiState.value = LocationScreenUiState(isLoading = true)
        viewModelScope.launch(coroutineExceptionHandler) {
            runCatching {
                fetchSavedLocationsUseCase.execute()
            }.onSuccess { results ->
                results.collect {
                    _uiState.value = LocationScreenUiState(list = it)
                }
            }.onFailure {
                _uiState.value = LocationScreenUiState(errorMessage = it.message ?: it.javaClass.name)
            }
        }
    }

    fun deleteLocation(locationForecast: LocationForecast) {
        viewModelScope.launch {
            runCatching {
                deleteLocationUseCase.execute(locationForecast)
            }.onFailure {
                _uiState.value = LocationScreenUiState(errorMessage = it.message ?: it.javaClass.name)
            }
        }
    }
}
