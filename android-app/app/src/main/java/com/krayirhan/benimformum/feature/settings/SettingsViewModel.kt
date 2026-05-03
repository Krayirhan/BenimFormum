package com.krayirhan.benimformum.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krayirhan.benimformum.domain.model.AppPreferences
import com.krayirhan.benimformum.domain.model.ThemePreference
import com.krayirhan.benimformum.domain.model.TrackedMetric
import com.krayirhan.benimformum.domain.usecase.ExportAllDataToJsonUseCase
import com.krayirhan.benimformum.domain.usecase.ExportDailyFormsToCsvUseCase
import com.krayirhan.benimformum.domain.usecase.ExportWaterLogsToCsvUseCase
import com.krayirhan.benimformum.domain.usecase.ObserveAppPreferencesUseCase
import com.krayirhan.benimformum.domain.usecase.SaveAppPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val exportDailyFormsToCsvUseCase: ExportDailyFormsToCsvUseCase,
    private val exportWaterLogsToCsvUseCase: ExportWaterLogsToCsvUseCase,
    private val exportAllDataToJsonUseCase: ExportAllDataToJsonUseCase,
    observeAppPreferencesUseCase: ObserveAppPreferencesUseCase,
    private val saveAppPreferencesUseCase: SaveAppPreferencesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var exportId = 0L

    init {
        viewModelScope.launch {
            observeAppPreferencesUseCase().collect { preferences ->
                _uiState.update { it.copy(preferences = preferences) }
            }
        }
    }

    fun onWaterGoalChanged(goalMl: Int) {
        updatePreferences { preferences ->
            preferences.copy(waterGoalMl = goalMl.coerceIn(MIN_WATER_GOAL_ML, MAX_WATER_GOAL_ML))
        }
    }

    fun onThemePreferenceChanged(themePreference: ThemePreference) {
        updatePreferences { preferences ->
            preferences.copy(themePreference = themePreference)
        }
    }

    fun onDynamicColorChanged(enabled: Boolean) {
        updatePreferences { preferences ->
            preferences.copy(dynamicColor = enabled)
        }
    }

    fun onTrackedMetricToggled(metric: TrackedMetric) {
        val currentMetrics = _uiState.value.preferences.trackedMetrics
        if (metric in currentMetrics && currentMetrics.size == 1) {
            _uiState.update { it.copy(message = "En az bir takip alanı açık kalmalı.") }
            return
        }

        updatePreferences { preferences ->
            val nextMetrics = if (metric in preferences.trackedMetrics) {
                preferences.trackedMetrics - metric
            } else {
                preferences.trackedMetrics + metric
            }
            preferences.copy(trackedMetrics = nextMetrics.ifEmpty { AppPreferences.DEFAULT_TRACKED_METRICS })
        }
    }

    fun prepareDailyFormsCsvExport() {
        prepareExport(
            kind = ExportKind.DAILY_FORMS_CSV,
            fileName = "benim-formum-daily-forms-${LocalDate.now()}.csv",
            mimeType = "text/csv"
        ) {
            exportDailyFormsToCsvUseCase()
        }
    }

    fun prepareWaterLogsCsvExport() {
        prepareExport(
            kind = ExportKind.WATER_LOGS_CSV,
            fileName = "benim-formum-water-logs-${LocalDate.now()}.csv",
            mimeType = "text/csv"
        ) {
            exportWaterLogsToCsvUseCase()
        }
    }

    fun prepareAllDataJsonExport() {
        prepareExport(
            kind = ExportKind.ALL_DATA_JSON,
            fileName = "benim-formum-export-${LocalDate.now()}.json",
            mimeType = "application/json"
        ) {
            exportAllDataToJsonUseCase()
        }
    }

    fun onExportLaunchConsumed() {
        _uiState.update { it.copy(pendingExport = null) }
    }

    fun onExportCancelled() {
        _uiState.update { it.copy(message = "Dışa aktarma iptal edildi.") }
    }

    fun onExportWriteResult(fileName: String, result: Result<Unit>) {
        _uiState.update {
            it.copy(
                message = result.exceptionOrNull()?.message
                    ?: "$fileName cihazına kaydedildi."
            )
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun prepareExport(
        kind: ExportKind,
        fileName: String,
        mimeType: String,
        createContent: suspend () -> String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(exportingKind = kind, pendingExport = null, message = null) }
            val result = runCatching { createContent() }
            _uiState.update { current ->
                val content = result.getOrNull()
                if (content == null) {
                    current.copy(
                        exportingKind = null,
                        message = result.exceptionOrNull()?.message ?: "Dışa aktarma hazırlanamadı."
                    )
                } else {
                    current.copy(
                        exportingKind = null,
                        pendingExport = PreparedExport(
                            id = nextExportId(),
                            kind = kind,
                            fileName = fileName,
                            mimeType = mimeType,
                            content = content
                        )
                    )
                }
            }
        }
    }

    private fun updatePreferences(transform: (AppPreferences) -> AppPreferences) {
        viewModelScope.launch {
            val nextPreferences = transform(_uiState.value.preferences)
            val result = runCatching { saveAppPreferencesUseCase(nextPreferences) }
            result.exceptionOrNull()?.let { exception ->
                _uiState.update { current ->
                    current.copy(message = exception.message ?: "Ayar kaydedilemedi.")
                }
            }
        }
    }

    private fun nextExportId(): Long {
        exportId += 1
        return exportId
    }

    private companion object {
        const val MIN_WATER_GOAL_ML = 1000
        const val MAX_WATER_GOAL_ML = 4000
    }
}
