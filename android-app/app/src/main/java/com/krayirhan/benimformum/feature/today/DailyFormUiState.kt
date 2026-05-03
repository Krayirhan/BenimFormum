package com.krayirhan.benimformum.feature.today

import com.krayirhan.benimformum.domain.model.AppPreferences
import com.krayirhan.benimformum.domain.model.TrackedMetric

data class DailyFormUiState(
    val date: String = "",
    val weightInput: String = "",
    val sleepQualityInput: String = "",
    val energyScoreInput: String = "",
    val moodScoreInput: String = "",
    val nightSnackDone: Boolean? = null,
    val noteInput: String = "",
    val waterTotalMl: Int = 0,
    val waterGoalMl: Int = AppPreferences.DEFAULT_WATER_GOAL_ML,
    val trackedMetrics: Set<TrackedMetric> = AppPreferences.DEFAULT_TRACKED_METRICS,
    val formScore: Int = 0,
    val isSaving: Boolean = false,
    val isAddingWater: Boolean = false,
    val message: String? = null
)
