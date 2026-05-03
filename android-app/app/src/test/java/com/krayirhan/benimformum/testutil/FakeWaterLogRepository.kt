package com.krayirhan.benimformum.testutil

import com.krayirhan.benimformum.domain.model.DailyWaterTotal
import com.krayirhan.benimformum.domain.model.WaterLog
import com.krayirhan.benimformum.domain.repository.WaterLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWaterLogRepository(
    private val totalsBetweenFlow: MutableStateFlow<List<DailyWaterTotal>> = MutableStateFlow(emptyList()),
    var capturedBetweenStart: String? = null,
    var capturedBetweenEnd: String? = null,
    val addedLogs: MutableList<WaterLog> = mutableListOf()
) : WaterLogRepository {

    override suspend fun addWaterLog(waterLog: WaterLog) {
        addedLogs += waterLog
    }

    override suspend fun getAllWaterLogs(): List<WaterLog> {
        return addedLogs.sortedBy { it.timestamp }
    }

    override fun getTodayWaterTotal(date: String): Flow<Int> =
        MutableStateFlow(0)

    override fun getDailyWaterTotalsBetween(startDate: String, endDate: String): Flow<List<DailyWaterTotal>> {
        capturedBetweenStart = startDate
        capturedBetweenEnd = endDate
        return totalsBetweenFlow
    }

    fun emitTotals(list: List<DailyWaterTotal>) {
        totalsBetweenFlow.value = list
    }
}
