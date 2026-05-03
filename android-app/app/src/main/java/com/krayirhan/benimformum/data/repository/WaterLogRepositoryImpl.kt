package com.krayirhan.benimformum.data.repository

import com.krayirhan.benimformum.data.local.dao.WaterLogDao
import com.krayirhan.benimformum.data.local.entity.WaterLogEntity
import com.krayirhan.benimformum.domain.model.DailyWaterTotal
import com.krayirhan.benimformum.domain.model.WaterLog
import com.krayirhan.benimformum.domain.repository.WaterLogRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WaterLogRepositoryImpl @Inject constructor(
    private val waterLogDao: WaterLogDao
) : WaterLogRepository {

    override suspend fun addWaterLog(waterLog: WaterLog) {
        waterLogDao.insertWaterLog(
            WaterLogEntity(
                date = waterLog.date,
                amountMl = waterLog.amountMl,
                timestamp = waterLog.timestamp,
                source = waterLog.source
            )
        )
    }

    override suspend fun getAllWaterLogs(): List<WaterLog> {
        return waterLogDao.getAllWaterLogs().map { it.toDomain() }
    }

    override fun getTodayWaterTotal(date: String): Flow<Int> {
        return waterLogDao.getTotalWaterByDateAsFlow(date)
    }

    override fun getDailyWaterTotalsBetween(startDate: String, endDate: String): Flow<List<DailyWaterTotal>> {
        return waterLogDao
            .getDailyWaterTotalsBetweenDatesAsFlow(startDate, endDate)
            .map { rows -> rows.map { DailyWaterTotal(date = it.date, totalMl = it.totalMl) } }
    }
}

private fun WaterLogEntity.toDomain(): WaterLog {
    return WaterLog(
        id = id,
        date = date,
        amountMl = amountMl,
        timestamp = timestamp,
        source = source
    )
}
