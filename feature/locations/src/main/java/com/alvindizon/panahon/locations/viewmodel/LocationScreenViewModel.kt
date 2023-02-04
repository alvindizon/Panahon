package com.alvindizon.panahon.locations.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.panahon.design.message.UiMessage
import com.alvindizon.panahon.locations.data.LocationsViewRepository
import com.alvindizon.panahon.locations.model.LocationForecast
import com.alvindizon.panahon.locations.usecase.FetchLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class LocationScreenUiState(
    val list: List<LocationForecast> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: UiMessage? = null
) {
    companion object {
        val Empty = LocationScreenUiState()
    }
}

@HiltViewModel
class LocationScreenViewModel @Inject constructor(
    private val repository: LocationsViewRepository,
    private val fetchLocationsUseCase: FetchLocationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationScreenUiState.Empty)
    val uiState: StateFlow<LocationScreenUiState> = _uiState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        handleError(exception)
    }

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            runCatching {
                fetchLocationsUseCase()
            }.onSuccess { results ->
                results.collectLatest { list ->
                    _uiState.value = LocationScreenUiState(list = list, isLoading = false)
                }
            }.onFailure(this@LocationScreenViewModel::handleError)
        }
    }

    fun deleteLocation(locationForecast: LocationForecast) {
        viewModelScope.launch(coroutineExceptionHandler) {
            runCatching {
                repository.deleteLocation(locationForecast)
            }.onFailure(this@LocationScreenViewModel::handleError)
        }
    }

    private fun handleError(error: Throwable) {
        _uiState.update {
            it.copy(
                errorMessage = UiMessage(error.message ?: error.javaClass.name),
                isLoading = false
            )
        }
    }
}
