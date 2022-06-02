package com.alvindizon.panahon.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.panahon.core.utils.getStateFlow
import com.alvindizon.panahon.ui.search.SearchResult
import com.alvindizon.panahon.usecase.SaveLocationToDbUseCase
import com.alvindizon.panahon.usecase.SearchForLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class SearchLocationUiState {
    object Empty : SearchLocationUiState()
    object Searching : SearchLocationUiState()
    class Success(val searchResults: List<SearchResult>) : SearchLocationUiState()
    class Error(val message: String) : SearchLocationUiState()
}

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val searchForLocationsUseCase: SearchForLocationsUseCase,
    private val saveLocationToDbUseCase: SaveLocationToDbUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(viewModelScope, "searchQuery", "")

    private val _searchLocationUiState =
        MutableStateFlow<SearchLocationUiState>(SearchLocationUiState.Empty)
    val searchLocationUiState: StateFlow<SearchLocationUiState> = _searchLocationUiState

    fun searchForLocations(query: String) {
        searchQuery.value = query
        viewModelScope.launch {
            runCatching {
                searchQuery.debounce(300L).filter { it.isNotEmpty() }
                    .flatMapLatest {
                        _searchLocationUiState.value = SearchLocationUiState.Searching
                        searchForLocationsUseCase.execute(it)
                    }
            }.onSuccess { results ->
                results.collect {
                    _searchLocationUiState.value = if (it.isEmpty()) {
                        SearchLocationUiState.Empty
                    } else {
                        SearchLocationUiState.Success(it)
                    }
                }
            }.onFailure {
                val message = it.message ?: it.javaClass.name
                _searchLocationUiState.value = SearchLocationUiState.Error(message)
            }
        }
    }

    fun clearQuery() {
        _searchLocationUiState.value = SearchLocationUiState.Empty
        searchQuery.value = ""
    }

    fun saveResultToDb(result: SearchResult) = viewModelScope.launch {
        saveLocationToDbUseCase.execute(result)
    }
}
