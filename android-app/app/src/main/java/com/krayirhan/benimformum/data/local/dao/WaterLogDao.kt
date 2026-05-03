package com.krayirhan.benimformum.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.krayirhan.benimformum.data.local.entity.WaterLogEntity
import com.krayirhan.benimformum.data.local.model.DailyWaterTotalRow
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterLogDao {
    @Insert
    suspend fun insertWaterLog(waterLog: WaterLogEntity)

    @Query("SELECT * FROM water_log WHERE date = :date ORDER BY timestamp DESC")
    fun getWaterLogsByDateAsFlow(date: String): Flow<List<WaterLogEntity>>

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM water_log WHERE date = :date")
    fun getTotalWaterByDateAsFlow(date: String): Flow<Int>

    @Query(
        "SELECT date, COALESCE(SUM(amountMl), 0) AS totalMl " +
            "FROM water_log WHERE date BETWEEN :startDate AND :endDate " +
            "GROUP BY date ORDER BY date DESC"
    )
    fun getDailyWaterTotalsBetweenDatesAsFlow(
        startDate: String,
        endDate: String
    ): Flow<List<DailyWaterTotalRow>>

    @Query("SELECT * FROM water_log ORDER BY timestamp ASC")
    suspend fun getAllWaterLogs(): List<WaterLogEntity>
}
