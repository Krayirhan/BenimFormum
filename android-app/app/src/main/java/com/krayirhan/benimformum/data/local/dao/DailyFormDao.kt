package com.krayirhan.benimformum.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krayirhan.benimformum.data.local.entity.DailyFormEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyFormDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyForm(form: DailyFormEntity)

    @Query("SELECT * FROM daily_form WHERE date = :date LIMIT 1")
    fun getDailyFormByDateAsFlow(date: String): Flow<DailyFormEntity?>

    @Query("SELECT * FROM daily_form WHERE date = :date LIMIT 1")
    suspend fun getDailyFormByDate(date: String): DailyFormEntity?

    @Query("SELECT * FROM daily_form WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getDailyFormsBetweenDatesAsFlow(startDate: String, endDate: String): Flow<List<DailyFormEntity>>

    @Query("SELECT * FROM daily_form ORDER BY date ASC")
    suspend fun getAllDailyForms(): List<DailyFormEntity>
}
