package com.krayirhan.benimformum.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krayirhan.benimformum.domain.model.AppPreferences
import com.krayirhan.benimformum.domain.usecase.CompleteOnboardingUseCase
import com.krayirhan.benimformum.domain.usecase.ObserveOnboardingCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AppNavViewModel @Inject constructor(
    observeOnboardingCompletedUseCase: ObserveOnboardingCompletedUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppNavUiState())
    val uiState: StateFlow<AppNavUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeOnboardingCompletedUseCase().collect { completed ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        onboardingCompleted = completed
                    )
                }
            }
        }
    }

    fun completeOnboarding(preferences: AppPreferences = AppPreferences()) {
        viewModelScope.launch {
            completeOnboardingUseCase(preferences)
        }
    }
}
