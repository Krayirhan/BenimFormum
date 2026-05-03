package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.model.AppPreferences
import com.krayirhan.benimformum.domain.model.DailyForm
import javax.inject.Inject

class CalculateFormScoreUseCase @Inject constructor() {

    operator fun invoke(
        form: DailyForm?,
        waterTotalMl: Int,
        waterGoalMl: Int = AppPreferences.DEFAULT_WATER_GOAL_ML
    ): Int {
        if (form == null) return 0

        var score = 0
        form.energyScore?.let {
            score += it.coerceIn(1, 10) * 4 // max 40
        }
        form.moodScore?.let {
            score += it.coerceIn(1, 5) * 6 // max 30
        }

        form.sleepQuality?.let {
            score += it.coerceIn(1, 5) * 4 // max 20
        }

        if (form.nightSnackDone == false) {
            score += 5
        }

        val waterRatio = waterTotalMl.toFloat() / waterGoalMl.coerceAtLeast(1)
        if (waterRatio >= 1f) {
            score += 5
        } else if (waterRatio >= 0.5f) {
            score += 3
        } else if (waterTotalMl > 0) {
            score += 1
        }

        return score.coerceIn(0, 100)
    }
}
