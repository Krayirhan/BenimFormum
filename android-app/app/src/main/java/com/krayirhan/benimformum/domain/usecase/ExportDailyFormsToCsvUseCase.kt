package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.repository.DailyFormRepository
import javax.inject.Inject

class ExportDailyFormsToCsvUseCase @Inject constructor(
    private val dailyFormRepository: DailyFormRepository
) {
    suspend operator fun invoke(): String {
        val forms = dailyFormRepository.getAllDailyForms()
        return buildString {
            appendLine(
                listOf(
                    "date",
                    "weight",
                    "sleepQuality",
                    "energyScore",
                    "moodScore",
                    "nightSnackDone",
                    "note",
                    "createdAt",
                    "updatedAt"
                ).joinToString(",")
            )
            forms.forEach { form ->
                appendLine(
                    listOf(
                        csvCell(form.date),
                        csvCell(form.weight),
                        csvCell(form.sleepQuality),
                        csvCell(form.energyScore),
                        csvCell(form.moodScore),
                        csvCell(form.nightSnackDone),
                        csvCell(form.note),
                        csvCell(form.createdAt),
                        csvCell(form.updatedAt)
                    ).joinToString(",")
                )
            }
        }
    }
}

internal fun csvCell(value: Any?): String {
    if (value == null) return ""
    val text = value.toString()
    val escaped = text.replace("\"", "\"\"")
    val needsQuotes = escaped.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
    return if (needsQuotes) "\"$escaped\"" else escaped
}
