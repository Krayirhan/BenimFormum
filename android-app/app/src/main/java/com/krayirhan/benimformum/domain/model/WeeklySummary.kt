package com.krayirhan.benimformum.domain.model

enum class WeeklySummaryTone {
    NEUTRAL,
    WATER,
    ENERGY,
    MOOD,
    SLEEP,
    WEIGHT,
    FOOD
}

data class WeeklySummaryItem(
    val title: String,
    val description: String,
    val metric: String? = null,
    val tone: WeeklySummaryTone = WeeklySummaryTone.NEUTRAL
)

data class WeeklySummary(
    val items: List<WeeklySummaryItem>,
    val hasData: Boolean,
    val headline: String = "",
    val helperText: String = "",
    val recordedDays: Int = 0
)
