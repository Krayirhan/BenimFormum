package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.model.DailyForm
import com.krayirhan.benimformum.domain.model.WaterLog
import com.krayirhan.benimformum.domain.repository.DailyFormRepository
import com.krayirhan.benimformum.domain.repository.WaterLogRepository
import javax.inject.Inject

class ExportAllDataToJsonUseCase @Inject constructor(
    private val dailyFormRepository: DailyFormRepository,
    private val waterLogRepository: WaterLogRepository
) {
    suspend operator fun invoke(): String {
        val forms = dailyFormRepository.getAllDailyForms()
        val waterLogs = waterLogRepository.getAllWaterLogs()
        return buildString {
            append("{\n")
            append("  \"schema\": \"benim-formum-export-v1\",\n")
            append("  \"exportedAt\": ${System.currentTimeMillis()},\n")
            append("  \"dailyForms\": [\n")
            forms.forEachIndexed { index, form ->
                append("    ")
                append(form.toJsonObject())
                append(if (index == forms.lastIndex) "\n" else ",\n")
            }
            append("  ],\n")
            append("  \"waterLogs\": [\n")
            waterLogs.forEachIndexed { index, log ->
                append("    ")
                append(log.toJsonObject())
                append(if (index == waterLogs.lastIndex) "\n" else ",\n")
            }
            append("  ]\n")
            append("}\n")
        }
    }
}

private fun DailyForm.toJsonObject(): String {
    return listOf(
        "\"date\": ${jsonString(date)}",
        "\"weight\": ${weight?.toString() ?: "null"}",
        "\"sleepQuality\": ${sleepQuality?.toString() ?: "null"}",
        "\"energyScore\": ${energyScore?.toString() ?: "null"}",
        "\"moodScore\": ${moodScore?.toString() ?: "null"}",
        "\"nightSnackDone\": ${nightSnackDone?.toString() ?: "null"}",
        "\"note\": ${note?.let { jsonString(it) } ?: "null"}",
        "\"createdAt\": $createdAt",
        "\"updatedAt\": $updatedAt"
    ).joinToString(prefix = "{ ", separator = ", ", postfix = " }")
}

private fun WaterLog.toJsonObject(): String {
    return listOf(
        "\"id\": $id",
        "\"date\": ${jsonString(date)}",
        "\"amountMl\": $amountMl",
        "\"timestamp\": $timestamp",
        "\"source\": ${jsonString(source)}"
    ).joinToString(prefix = "{ ", separator = ", ", postfix = " }")
}

private fun jsonString(value: String): String {
    return buildString {
        append('"')
        value.forEach { char ->
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                '\b' -> append("\\b")
                else -> append(char)
            }
        }
        append('"')
    }
}
