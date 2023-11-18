package com.alvindizon.panahon.searchlocation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.panahon.core.android.utils.getStateFlow
import com.alvindizon.panahon.data.location.LocationRepository
import com.alvindizon.panahon.data.location.model.CurrentLocation
import com.alvindizon.panahon.data.location.model.SearchResult
import com.alvindizon.panahon.design.message.UiMessage
import com.alvindizon.panahon.searchlocation.usecase.SaveLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchLocationUiState(
    val searchResults: List<SearchResult>? = null,
    val isLoading: Boolean = false,
    val errorMessage: UiMessage? = null,
    val locationSettingsNotEnabled: Boolean = false,
    val locationNotFound: Boolean = false,
    val currentLocation: CurrentLocation? = null
) {
    companion object {
        val Empty = SearchLocationUiState()
    }
}

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val saveLocationUseCase: SaveLocationUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(viewModelScope, QUERY_KEY, "")

    private val _uiState = MutableStateFlow(SearchLocationUiState.Empty)
    val uiState: StateFlow<SearchLocationUiState> = _uiState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        handleError(exception)
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
        viewModelScope.launch(coroutineExceptionHandler) {
            runCatching {
                searchQuery.debounce(300L)
                    .filter { it.isNotEmpty() }
                    .flatMapLatest {
                        _uiState.value = SearchLocationUiState(isLoading = true)
                        locationRepository.searchForLocation(it)
                    }
            }.onSuccess {
                it.collectLatest { results ->
                    _uiState.update { state ->
                        state.copy(
                            searchResults = results,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            }.onFailure {
                handleError(it)
            }
        }
    }

    fun clearQuery() {
        searchQuery.value = ""
    }

    fun onSearchResultClick(result: SearchResult) = viewModelScope.launch(coroutineExceptionHandler) {
        runCatching {
            saveLocationUseCase(result.locationName, result.lat, result.lon)
        }.onFailure {
            handleError(it)
        }
    }

    /**
     * Gets the current location.
     */
    fun onFetchLocationClick() = viewModelScope.launch(coroutineExceptionHandler) {
        val result = locationRepository.getCurrentLocation()
        when {
            result?.isLocationEnabled == true && result.currentLocation != null -> {
                _uiState.update { state -> state.copy(currentLocation = result.currentLocation, locationNotFound = false, locationSettingsNotEnabled = false) }
            }
            result?.isLocationEnabled == true && result.currentLocation == null ->
                _uiState.update { state -> state.copy(locationNotFound = true) }
            result?.isLocationEnabled == false ->
                _uiState.update { state -> state.copy(locationSettingsNotEnabled = true) }
        }
    }

    private fun handleError(error: Throwable) {
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                errorMessage = UiMessage(error.message ?: error.javaClass.name)
            )
        }
    }

    companion object {
        private const val QUERY_KEY = "searchQuery"
    }
}
