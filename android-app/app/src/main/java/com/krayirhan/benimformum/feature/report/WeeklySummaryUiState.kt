package com.krayirhan.benimformum.feature.report

import com.krayirhan.benimformum.domain.model.WeeklySummaryItem

data class WeeklySummaryUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val hasData: Boolean = false,
    val headline: String = "",
    val helperText: String = "",
    val recordedDays: Int = 0,
    val items: List<WeeklySummaryItem> = emptyList()
)
