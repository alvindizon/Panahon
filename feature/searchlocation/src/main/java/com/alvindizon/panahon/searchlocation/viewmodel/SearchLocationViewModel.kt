package com.alvindizon.panahon.searchlocation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.panahon.core.android.utils.getStateFlow
import com.alvindizon.panahon.design.message.UiMessage
import com.alvindizon.panahon.searchlocation.data.SearchLocationViewRepository
import com.alvindizon.panahon.searchlocation.model.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchLocationUiState(
    val searchResults: List<SearchResult>? = null,
    val isLoading: Boolean = false,
    val errorMessage: UiMessage? = null
) {
    companion object {
        val Empty = SearchLocationUiState()
    }
}

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val repository: SearchLocationViewRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(viewModelScope, QUERY_KEY, "")

    private val _uiState = MutableStateFlow(SearchLocationUiState.Empty)
    val uiState: StateFlow<SearchLocationUiState> = _uiState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        handleError(exception)
    }

    fun searchForLocations(query: String) {
        searchQuery.value = query
        viewModelScope.launch(coroutineExceptionHandler) {
            runCatching {
                searchQuery.debounce(300L)
                    .filter { it.isNotEmpty() }
                    .flatMapLatest {
                        _uiState.value = SearchLocationUiState(isLoading = true)
                        repository.searchForLocation(it)
                    }
            }.onSuccess {
                it.collectLatest { results ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = results,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure {
                handleError(it)
            }
        }
    }

    fun clearQuery() {
        searchQuery.value = ""
    }

    fun saveResultToDb(result: SearchResult) = viewModelScope.launch(coroutineExceptionHandler) {
        runCatching {
            repository.saveLocationToDatabase(result.locationName, result.lat, result.lon)
        }.onFailure { handleError(it) }
    }

    private fun handleError(error: Throwable) {
        _uiState.value =
            _uiState.value.copy(
                isLoading = false,
                errorMessage = UiMessage(error.message ?: error.javaClass.name)
            )
    }

    companion object {
        private const val QUERY_KEY = "searchQuery"
    }
}
