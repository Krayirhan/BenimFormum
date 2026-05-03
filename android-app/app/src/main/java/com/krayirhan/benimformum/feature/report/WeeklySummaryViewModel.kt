package com.krayirhan.benimformum.feature.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krayirhan.benimformum.domain.usecase.GetWeeklySummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class WeeklySummaryViewModel @Inject constructor(
    private val getWeeklySummaryUseCase: GetWeeklySummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklySummaryUiState())
    val uiState: StateFlow<WeeklySummaryUiState> = _uiState.asStateFlow()
    private val refreshSignal = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            refreshSignal.flatMapLatest {
                getWeeklySummaryUseCase()
            }.collect { summary ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        hasData = summary.hasData,
                        headline = summary.headline,
                        helperText = summary.helperText,
                        recordedDays = summary.recordedDays,
                        items = summary.items
                    )
                }
            }
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        refreshSignal.update { it + 1 }
    }
}
