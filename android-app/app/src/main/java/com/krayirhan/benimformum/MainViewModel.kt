package com.krayirhan.benimformum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krayirhan.benimformum.domain.model.ThemePreference
import com.krayirhan.benimformum.domain.usecase.ObserveAppPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val dynamicColor: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    observeAppPreferencesUseCase: ObserveAppPreferencesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeAppPreferencesUseCase().collect { preferences ->
                _uiState.update {
                    it.copy(
                        themePreference = preferences.themePreference,
                        dynamicColor = preferences.dynamicColor
                    )
                }
            }
        }
    }
}
