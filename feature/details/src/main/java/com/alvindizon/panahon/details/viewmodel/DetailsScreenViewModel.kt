package com.alvindizon.panahon.details.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.panahon.details.model.DetailedForecast
import com.alvindizon.panahon.details.navigation.DetailsNavigation
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

//data class NavArgs(
//    val locationName: String = "",
//    val latitude: String = "",
//    val longitude: String = ""
//)

@HiltViewModel
class DetailsScreenViewModel @Inject constructor(
    private val fetchDetailedForecastUseCase: FetchDetailedForecastUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

//    var navArgs = mutableStateOf(NavArgs())
//        private set
    var locationName: String? = null
    var latitude: String? = null
    var longitude: String? = null

    private val _uiState = MutableStateFlow<DetailsScreenUiState>(DetailsScreenUiState.Empty)
    val uiState: StateFlow<DetailsScreenUiState> = _uiState

    init {
        locationName = savedStateHandle.get<String>(DetailsNavigation.locationArg)
        latitude = savedStateHandle.get<String>(DetailsNavigation.latitudeArg)
        longitude = savedStateHandle.get<String>(DetailsNavigation.longitudeArg)
    }

    fun fetchDetailedForecast() {
        _uiState.value = DetailsScreenUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                fetchDetailedForecastUseCase.execute(locationName!!, latitude!!, longitude!!)
            }.onSuccess { detailedForecast ->
                _uiState.value = DetailsScreenUiState.Success(detailedForecast)
            }.onFailure {
                _uiState.value = DetailsScreenUiState.Error(it.message ?: it.javaClass.name)
            }
        }
    }
}
