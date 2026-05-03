package com.krayirhan.benimformum.feature.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krayirhan.benimformum.domain.usecase.AddWaterLogUseCase
import com.krayirhan.benimformum.domain.usecase.CalculateFormScoreUseCase
import com.krayirhan.benimformum.domain.usecase.GetTodayDailyFormUseCase
import com.krayirhan.benimformum.domain.usecase.GetTodayWaterTotalUseCase
import com.krayirhan.benimformum.domain.usecase.ObserveAppPreferencesUseCase
import com.krayirhan.benimformum.domain.usecase.SaveDailyFormUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DailyFormViewModel @Inject constructor(
    getTodayDailyFormUseCase: GetTodayDailyFormUseCase,
    getTodayWaterTotalUseCase: GetTodayWaterTotalUseCase,
    observeAppPreferencesUseCase: ObserveAppPreferencesUseCase,
    private val calculateFormScoreUseCase: CalculateFormScoreUseCase,
    private val addWaterLogUseCase: AddWaterLogUseCase,
    private val saveDailyFormUseCase: SaveDailyFormUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyFormUiState())
    val uiState: StateFlow<DailyFormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getTodayDailyFormUseCase().collect { form ->
                _uiState.update {
                    if (form == null) {
                        it.copy(
                            date = "",
                            formScore = 0
                        )
                    } else {
                        it.copy(
                            date = form.date,
                            weightInput = form.weight?.toString().orEmpty(),
                            sleepQualityInput = form.sleepQuality?.toString().orEmpty(),
                            energyScoreInput = form.energyScore?.toString().orEmpty(),
                            moodScoreInput = form.moodScore?.toString().orEmpty(),
                            nightSnackDone = form.nightSnackDone,
                            noteInput = form.note.orEmpty(),
                            formScore = calculateFormScoreUseCase(form, it.waterTotalMl, it.waterGoalMl)
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            getTodayWaterTotalUseCase().collect { totalMl ->
                _uiState.update { current ->
                    val form = current.toFormOrNull()
                    current.copy(
                        waterTotalMl = totalMl,
                        formScore = calculateFormScoreUseCase(form, totalMl, current.waterGoalMl)
                    )
                }
            }
        }

        viewModelScope.launch {
            observeAppPreferencesUseCase().collect { preferences ->
                _uiState.update { current ->
                    val form = current.toFormOrNull()
                    current.copy(
                        waterGoalMl = preferences.waterGoalMl,
                        trackedMetrics = preferences.trackedMetrics,
                        formScore = calculateFormScoreUseCase(
                            form = form,
                            waterTotalMl = current.waterTotalMl,
                            waterGoalMl = preferences.waterGoalMl
                        )
                    )
                }
            }
        }
    }

    fun onWeightChanged(value: String) = _uiState.update { it.copy(weightInput = value) }
    fun onSleepQualityChanged(value: String) = _uiState.update { it.copy(sleepQualityInput = value) }
    fun onEnergyChanged(value: String) = _uiState.update { it.copy(energyScoreInput = value) }
    fun onMoodChanged(value: String) = _uiState.update { it.copy(moodScoreInput = value) }
    fun onNightSnackChanged(value: Boolean) = _uiState.update { it.copy(nightSnackDone = value) }
    fun onNoteChanged(value: String) = _uiState.update { it.copy(noteInput = value) }
    fun clearMessage() = _uiState.update { it.copy(message = null) }

    fun saveTodayForm() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, message = null) }
            val state = _uiState.value
            val result = saveDailyFormUseCase(
                weightInput = state.weightInput,
                sleepQualityInput = state.sleepQualityInput,
                energyScoreInput = state.energyScoreInput,
                moodScoreInput = state.moodScoreInput,
                nightSnackDone = state.nightSnackDone,
                noteInput = state.noteInput
            )
            _uiState.update {
                it.copy(
                    isSaving = false,
                    message = result.exceptionOrNull()?.message ?: "✅ Bugünkü form kaydedildi"
                )
            }
        }
    }

    fun addWaterQuick250() {
        addWaterQuick(250)
    }

    fun addWaterQuick500() {
        addWaterQuick(500)
    }

    private fun addWaterQuick(amountMl: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAddingWater = true, message = null) }
            val result = addWaterLogUseCase(amountMl)
            _uiState.update {
                it.copy(
                    isAddingWater = false,
                    message = result.exceptionOrNull()?.message ?: "💧 $amountMl ml eklendi"
                )
            }
        }
    }

    private fun DailyFormUiState.toFormOrNull(): com.krayirhan.benimformum.domain.model.DailyForm? {
        val energy = energyScoreInput.toIntOrNull()
        val mood = moodScoreInput.toIntOrNull()
        return com.krayirhan.benimformum.domain.model.DailyForm(
            date = date.ifBlank { return null },
            weight = weightInput.toDoubleOrNull(),
            sleepQuality = sleepQualityInput.toIntOrNull(),
            energyScore = energy,
            moodScore = mood,
            nightSnackDone = nightSnackDone,
            note = noteInput.ifBlank { null },
            createdAt = 0L,
            updatedAt = 0L
        )
    }
}
