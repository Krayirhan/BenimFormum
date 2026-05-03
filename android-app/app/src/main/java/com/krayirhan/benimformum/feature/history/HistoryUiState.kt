package com.krayirhan.benimformum.feature.history

import com.krayirhan.benimformum.domain.model.AppPreferences
import com.krayirhan.benimformum.domain.usecase.WeightTrendPoint

enum class HistoryRange(val days: Int) {
    LAST_7(7),
    LAST_30(30)
}

data class HistoryEntryUi(
    val date: String,
    val weightText: String,
    val sleepText: String,
    val energyText: String,
    val moodText: String,
    val nightSnackText: String,
    val waterText: String,
    val waterMl: Int = 0
)

data class HistoryInsightUi(
    val title: String = "",
    val description: String = ""
)

data class WaterHistoryStats(
    val recordedDays: Int = 0,
    val averageMl: Int = 0,
    val minMl: Int = 0,
    val maxMl: Int = 0,
    val targetReachedDays: Int = 0,
    val goalMl: Int = AppPreferences.DEFAULT_WATER_GOAL_ML
)

data class WeightHistoryStats(
    val recordedDays: Int = 0,
    val averageKg: Double? = null,
    val minKg: Double? = null,
    val maxKg: Double? = null,
    val deltaKg: Double? = null
)

data class HistoryUiState(
    val selectedRange: HistoryRange = HistoryRange.LAST_7,
    val entries: List<HistoryEntryUi> = emptyList(),
    val weightTrend: List<WeightTrendPoint> = emptyList(),
    val waterStats: WaterHistoryStats = WaterHistoryStats(),
    val weightStats: WeightHistoryStats = WeightHistoryStats(),
    val insight: HistoryInsightUi = HistoryInsightUi(),
    val isEmpty: Boolean = true
)
