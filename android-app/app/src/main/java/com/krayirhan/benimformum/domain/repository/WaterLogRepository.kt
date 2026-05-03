package com.krayirhan.benimformum.domain.repository

import com.krayirhan.benimformum.domain.model.WaterLog
import com.krayirhan.benimformum.domain.model.DailyWaterTotal
import kotlinx.coroutines.flow.Flow

interface WaterLogRepository {
    suspend fun addWaterLog(waterLog: WaterLog)
    suspend fun getAllWaterLogs(): List<WaterLog>
    fun getTodayWaterTotal(date: String): Flow<Int>
    fun getDailyWaterTotalsBetween(startDate: String, endDate: String): Flow<List<DailyWaterTotal>>
}
