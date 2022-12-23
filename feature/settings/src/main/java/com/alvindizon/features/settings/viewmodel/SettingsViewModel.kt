package com.alvindizon.features.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.features.settings.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = false,
    val preferredTempUnitIndex: Int = 0,
    val preferredSpeedUnitIndex: Int = 0,
    val preferredPressureUnitIndex: Int = 0,
    val preferredDistanceUnitIndex: Int = 0,
    val errorMessage: String? = null
)

// TODO try to find a way to generalize fetching units into a single usecase
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val fetchPreferredTempIndexUseCase: FetchPreferredTempIndexUseCase,
    private val fetchPreferredSpeedIndexUseCase: FetchPreferredSpeedIndexUseCase,
    private val fetchPreferredPressureIndexUseCase: FetchPreferredPressureIndexUseCase,
    private val fetchPreferredDistanceIndexUseCase: FetchPreferredDistanceIndexUseCase,
    private val setPreferredUnitUseCase: SetPreferredUnitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        // need to set this outside for unit test to pass
        _uiState.value = SettingsUiState(isLoading = true)
        viewModelScope.launch {
            runCatching {
                awaitAll(
                    async { fetchPreferredTempIndexUseCase() },
                    async { fetchPreferredSpeedIndexUseCase() },
                    async { fetchPreferredPressureIndexUseCase() },
                    async { fetchPreferredDistanceIndexUseCase() }
                )
            }.onSuccess { indices ->
                _uiState.value = SettingsUiState(
                    preferredTempUnitIndex = indices[0],
                    preferredSpeedUnitIndex = indices[1],
                    preferredPressureUnitIndex = indices[2],
                    preferredDistanceUnitIndex = indices[3]
                )
            }.onFailure {
                // if any of the above usecases fails, a CancellationException occurs which
                // has an error message of "StandaloneCoroutine is Cancelling". This doesn't help the
                // user if this is displayed, thus we replace it with something more human friendly
                val message = if (it is CancellationException) {
                    "An error occurred, please try again"
                } else {
                    it.message
                }
                _uiState.value = SettingsUiState(errorMessage = message)
            }
        }
    }

    fun setPreferredUnit(sign: String) {
        viewModelScope.launch {
            runCatching {
                setPreferredUnitUseCase(sign)
            }.onFailure {
                _uiState.value = SettingsUiState(errorMessage = it.message)
            }
        }
    }
}