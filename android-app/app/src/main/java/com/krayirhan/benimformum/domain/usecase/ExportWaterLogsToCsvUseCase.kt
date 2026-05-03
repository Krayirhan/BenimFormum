package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.repository.WaterLogRepository
import javax.inject.Inject

class ExportWaterLogsToCsvUseCase @Inject constructor(
    private val waterLogRepository: WaterLogRepository
) {
    suspend operator fun invoke(): String {
        val logs = waterLogRepository.getAllWaterLogs()
        return buildString {
            appendLine("id,date,amountMl,timestamp,source")
            logs.forEach { log ->
                appendLine(
                    listOf(
                        csvCell(log.id),
                        csvCell(log.date),
                        csvCell(log.amountMl),
                        csvCell(log.timestamp),
                        csvCell(log.source)
                    ).joinToString(",")
                )
            }
        }
    }
}
