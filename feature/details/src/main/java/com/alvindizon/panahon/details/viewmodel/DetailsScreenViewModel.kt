package com.alvindizon.panahon.details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.panahon.details.model.DetailedForecast
import com.alvindizon.panahon.details.usecase.FetchDetailedForecastUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface DetailsScreenUiState {
    object Empty : DetailsScreenUiState
    object Loading : DetailsScreenUiState
    class Success(val detailedForecast: DetailedForecast) : DetailsScreenUiState
    class Error(val message: String) : DetailsScreenUiState
}

@HiltViewModel
class DetailsScreenViewModel @Inject constructor(
    private val fetchDetailedForecastUseCase: FetchDetailedForecastUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsScreenUiState>(DetailsScreenUiState.Empty)
    val uiState: StateFlow<DetailsScreenUiState> = _uiState

    fun fetchDetailedForecast(name: String, latitude: String, longitude: String) {
        _uiState.value = DetailsScreenUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                fetchDetailedForecastUseCase.execute(name, latitude, longitude)
            }.onSuccess { detailedForecast ->
                _uiState.value = DetailsScreenUiState.Success(detailedForecast)
            }.onFailure {
                _uiState.value = DetailsScreenUiState.Error(it.message ?: it.javaClass.name)
            }
        }
    }
}
