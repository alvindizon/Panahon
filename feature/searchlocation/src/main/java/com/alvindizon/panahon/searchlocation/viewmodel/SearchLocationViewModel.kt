package com.alvindizon.panahon.searchlocation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.panahon.core.utils.getStateFlow
import com.alvindizon.panahon.design.message.UiMessage
import com.alvindizon.panahon.searchlocation.model.SearchResult
import com.alvindizon.panahon.searchlocation.usecase.SaveLocationToDbUseCase
import com.alvindizon.panahon.searchlocation.usecase.SearchForLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchLocationUiState(
    val searchQuery: String = "",
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
    private val searchForLocationsUseCase: SearchForLocationsUseCase,
    private val saveLocationToDbUseCase: SaveLocationToDbUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val searchQuery: MutableStateFlow<String> =
        savedStateHandle.getStateFlow(viewModelScope, "searchQuery", "")
    private val isLoadingFlow = MutableStateFlow(true)
    private val errorMessageFlow = MutableStateFlow<UiMessage?>(null)

    val uiState: StateFlow<SearchLocationUiState> = combine(
        searchQuery,
        searchForLocationsUseCase.flow,
        isLoadingFlow,
        errorMessageFlow,
        ::SearchLocationUiState
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SearchLocationUiState.Empty
    )

    init {
        viewModelScope.launch {
            searchQuery.debounce(300L)
                .onEach { query ->
                    val job = launch {
                        isLoadingFlow.value = true
                        searchForLocationsUseCase(query)
                    }
                    job.invokeOnCompletion { isLoadingFlow.value = false }
                    job.join()
                }
                .catch {
                    errorMessageFlow.value = UiMessage(it.message ?: it.javaClass.name)
                }
                .collect()
        }
    }

    fun searchForLocations(query: String) {
        searchQuery.value = query
    }

    fun clearQuery() {
        searchQuery.value = ""
    }

    fun saveResultToDb(result: SearchResult) = viewModelScope.launch {
        saveLocationToDbUseCase.execute(result)
    }
}
