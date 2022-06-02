package com.alvindizon.panahon.locations.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.locations.usecase.FetchForecastsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class LocationScreenUiState {
    object Empty : LocationScreenUiState()
    object Loading : LocationScreenUiState()
    class Success(val list: List<LocationForecast>) : LocationScreenUiState()
    class Error(val message: String) : LocationScreenUiState()
}

@HiltViewModel
class LocationScreenViewModel @Inject constructor(
    private val fetchForecastsUseCase: FetchForecastsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LocationScreenUiState>(LocationScreenUiState.Empty)
    val locationScreenUiState: StateFlow<LocationScreenUiState> = _uiState

    fun fetchForecasts() {
        _uiState.value = LocationScreenUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                fetchForecastsUseCase.execute()
            }.onSuccess { forecasts ->
                forecasts.collect {
                    _uiState.value = LocationScreenUiState.Success(it)
                }
            }.onFailure {
                _uiState.value = LocationScreenUiState.Error(it.message ?: it.javaClass.name)
            }
        }
    }
}
