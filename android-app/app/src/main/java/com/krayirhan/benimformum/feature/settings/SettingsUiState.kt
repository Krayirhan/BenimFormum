package com.krayirhan.benimformum.feature.settings

import com.krayirhan.benimformum.domain.model.AppPreferences

enum class ExportKind {
    DAILY_FORMS_CSV,
    WATER_LOGS_CSV,
    ALL_DATA_JSON
}

data class PreparedExport(
    val id: Long,
    val kind: ExportKind,
    val fileName: String,
    val mimeType: String,
    val content: String
)

data class SettingsUiState(
    val preferences: AppPreferences = AppPreferences(),
    val exportingKind: ExportKind? = null,
    val pendingExport: PreparedExport? = null,
    val message: String? = null
)
