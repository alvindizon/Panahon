package com.alvindizon.features.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.features.settings.usecase.FetchPreferredUnitsUseCase
import com.alvindizon.features.settings.usecase.SetPreferredUnitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
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

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val fetchPreferredUnitsUseCase: FetchPreferredUnitsUseCase,
    private val setPreferredUnitUseCase: SetPreferredUnitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        // need to set this outside for unit test to pass
        _uiState.value = SettingsUiState(isLoading = true)
        viewModelScope.launch {
            runCatching {
                fetchPreferredUnitsUseCase()
            }.onSuccess {
                it.collectLatest { indices ->
                    _uiState.value = SettingsUiState(
                        preferredTempUnitIndex = indices[0],
                        preferredSpeedUnitIndex = indices[1],
                        preferredPressureUnitIndex = indices[2],
                        preferredDistanceUnitIndex = indices[3]
                    )
                }
            }.onFailure {
                _uiState.value = SettingsUiState(errorMessage = it.message)
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