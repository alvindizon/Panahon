package com.alvindizon.features.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvindizon.features.settings.usecase.FetchPreferredTempIndexUseCase
import com.alvindizon.features.settings.usecase.SetPreferredTempUnitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = false,
    val preferredTempUnitIndex: Int = 0,
    val errorMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val fetchPreferredTempIndexUseCase: FetchPreferredTempIndexUseCase,
    private val setPreferredTempUnitUseCase: SetPreferredTempUnitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    fun fetchPreferredTemperatureUnit() {
        // need to set this outside for unit test to pass
        _uiState.value = SettingsUiState(isLoading = true)
        viewModelScope.launch {
            runCatching {
                fetchPreferredTempIndexUseCase.execute()
            }.onSuccess { index ->
                _uiState.value = SettingsUiState(preferredTempUnitIndex = index)
            }.onFailure {
                _uiState.value = SettingsUiState(errorMessage = it.message)
            }
        }
    }

    fun setPreferredTemperatureUnit(selectedIndex: Int) {
        viewModelScope.launch {
            runCatching {
                setPreferredTempUnitUseCase.execute(selectedIndex)
            }.onFailure {
                _uiState.value = SettingsUiState(errorMessage = it.message)
            }
        }
    }
}