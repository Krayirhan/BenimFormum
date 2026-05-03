package com.krayirhan.benimformum.domain.model

data class AppPreferences(
    val waterGoalMl: Int = DEFAULT_WATER_GOAL_ML,
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    /** Android 12+ duvar kağıdı renkleri; metrik renkleri [AppColorScheme] sabit kalır. */
    val dynamicColor: Boolean = false,
    val trackedMetrics: Set<TrackedMetric> = DEFAULT_TRACKED_METRICS
) {
    companion object {
        const val DEFAULT_WATER_GOAL_ML = 2000
        val DEFAULT_TRACKED_METRICS: Set<TrackedMetric> = TrackedMetric.values().toSet()
    }
}

enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK
}

enum class TrackedMetric {
    WATER,
    WEIGHT,
    SLEEP,
    ENERGY,
    MOOD,
    NIGHT_SNACK
}
