package com.krayirhan.benimformum.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krayirhan.benimformum.domain.model.DailyForm
import com.krayirhan.benimformum.domain.model.DailyWaterTotal
import com.krayirhan.benimformum.domain.usecase.CalculateWeightMovingAverageUseCase
import com.krayirhan.benimformum.domain.usecase.GetDailyWaterTotalsUseCase
import com.krayirhan.benimformum.domain.usecase.GetHistoryDailyFormsUseCase
import com.krayirhan.benimformum.domain.usecase.ObserveAppPreferencesUseCase
import com.krayirhan.benimformum.domain.usecase.WeightTrendPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class HistoryProjection(
    val entries: List<HistoryEntryUi>,
    val trend: List<WeightTrendPoint>,
    val waterStats: WaterHistoryStats,
    val weightStats: WeightHistoryStats,
    val insight: HistoryInsightUi,
    val isEmpty: Boolean
)

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModel @Inject constructor(
    private val getHistoryDailyFormsUseCase: GetHistoryDailyFormsUseCase,
    private val getDailyWaterTotalsUseCase: GetDailyWaterTotalsUseCase,
    private val calculateWeightMovingAverageUseCase: CalculateWeightMovingAverageUseCase,
    observeAppPreferencesUseCase: ObserveAppPreferencesUseCase
) : ViewModel() {

    private val selectedRangeFlow = MutableStateFlow(HistoryRange.LAST_7)
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                selectedRangeFlow.flatMapLatest { range ->
                    getHistoryDailyFormsUseCase(range.days)
                },
                selectedRangeFlow.flatMapLatest { range ->
                    getDailyWaterTotalsUseCase(range.days)
                },
                observeAppPreferencesUseCase()
            ) { forms, waterTotals, preferences ->
                val goalMl = preferences.waterGoalMl.coerceAtLeast(1)
                val entries = createEntries(forms, waterTotals)
                val trend = calculateWeightMovingAverageUseCase(forms)
                val waterStats = createWaterStats(waterTotals, goalMl)
                val weightStats = createWeightStats(forms)
                HistoryProjection(
                    entries = entries,
                    trend = trend,
                    waterStats = waterStats,
                    weightStats = weightStats,
                    insight = createInsight(
                        range = selectedRangeFlow.value,
                        entries = entries,
                        trend = trend,
                        waterStats = waterStats,
                        weightStats = weightStats
                    ),
                    isEmpty = forms.isEmpty() && waterTotals.isEmpty()
                )
            }.collect { projection ->
                _uiState.update {
                    it.copy(
                        selectedRange = selectedRangeFlow.value,
                        entries = projection.entries,
                        weightTrend = projection.trend,
                        waterStats = projection.waterStats,
                        weightStats = projection.weightStats,
                        insight = projection.insight,
                        isEmpty = projection.isEmpty
                    )
                }
            }
        }
    }

    fun onRangeSelected(range: HistoryRange) {
        selectedRangeFlow.value = range
    }

    private fun createEntries(
        forms: List<DailyForm>,
        waterTotals: List<DailyWaterTotal>
    ): List<HistoryEntryUi> {
        val formByDate = forms.associateBy { it.date }
        val waterByDate = waterTotals.associate { it.date to it.totalMl }
        val dates = (formByDate.keys + waterByDate.keys).distinct().sortedDescending()
        return dates.map { date ->
            val form = formByDate[date]
            val ml = waterByDate[date] ?: 0
            HistoryEntryUi(
                date = date,
                weightText = form?.weight?.toString() ?: "-",
                sleepText = form?.sleepQuality?.let { "$it / 5" } ?: "—",
                energyText = form?.energyScore?.let { "$it / 10" } ?: "—",
                moodText = form?.moodScore?.let { "$it / 5" } ?: "—",
                nightSnackText = when (form?.nightSnackDone) {
                    true -> "Evet"
                    false -> "Hayır"
                    null -> "—"
                },
                waterText = "$ml ml",
                waterMl = ml
            )
        }
    }

    private fun createWaterStats(
        waterTotals: List<DailyWaterTotal>,
        goalMl: Int
    ): WaterHistoryStats {
        val values = waterTotals.map { it.totalMl }
        if (values.isEmpty()) {
            return WaterHistoryStats(goalMl = goalMl)
        }

        return WaterHistoryStats(
            recordedDays = values.size,
            averageMl = values.average().toInt(),
            minMl = values.min(),
            maxMl = values.max(),
            targetReachedDays = values.count { it >= goalMl },
            goalMl = goalMl
        )
    }

    private fun createWeightStats(forms: List<DailyForm>): WeightHistoryStats {
        val weights = forms
            .sortedBy { it.date }
            .mapNotNull { it.weight }
        if (weights.isEmpty()) return WeightHistoryStats()

        return WeightHistoryStats(
            recordedDays = weights.size,
            averageKg = weights.average(),
            minKg = weights.min(),
            maxKg = weights.max(),
            deltaKg = if (weights.size >= 2) weights.last() - weights.first() else null
        )
    }

    private fun createInsight(
        range: HistoryRange,
        entries: List<HistoryEntryUi>,
        trend: List<WeightTrendPoint>,
        waterStats: WaterHistoryStats,
        weightStats: WeightHistoryStats
    ): HistoryInsightUi {
        val rangeLabel = if (range == HistoryRange.LAST_7) "son 7 gün" else "son 30 gün"
        return when {
            entries.size < 3 -> HistoryInsightUi(
                title = "Veri henüz az",
                description = "$rangeLabel içinde ${entries.size} gün veri var. Grafikler kesin sonuç değil, yalnızca başlangıç sinyali olarak okunmalı."
            )

            weightStats.recordedDays in 1..2 && trend.isEmpty() -> HistoryInsightUi(
                title = "Kilo trendi için biraz daha veri gerekir",
                description = "3 günlük hareketli ortalama için en az 3 kilo kaydı gerekir. Mevcut kayıtlar günlük kartlarda saklanıyor."
            )

            waterStats.recordedDays > 0 && waterStats.targetReachedDays == 0 -> HistoryInsightUi(
                title = "Su hedefi ayar sinyali veriyor",
                description = "$rangeLabel içinde su hedefine ulaşılan gün yok. Hedefini veya kayıt rutinini Ayarlar’dan gözden geçirebilirsin."
            )

            waterStats.targetReachedDays > 0 -> HistoryInsightUi(
                title = "Hedefe ulaşılan günler var",
                description = "$rangeLabel içinde su hedefin ${waterStats.targetReachedDays} gün karşılandı. Diğer veriler günlük ayrıntılarda listelenir."
            )

            else -> HistoryInsightUi(
                title = "Kayıtların okunabilir durumda",
                description = "$rangeLabel için grafikler ve günlük ayrıntılar birlikte gösteriliyor."
            )
        }
    }
}
