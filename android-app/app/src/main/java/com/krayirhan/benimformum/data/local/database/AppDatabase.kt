package com.krayirhan.benimformum.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.krayirhan.benimformum.data.local.dao.DailyFormDao
import com.krayirhan.benimformum.data.local.dao.WaterLogDao
import com.krayirhan.benimformum.data.local.entity.DailyFormEntity
import com.krayirhan.benimformum.data.local.entity.WaterLogEntity

@Database(
    entities = [DailyFormEntity::class, WaterLogEntity::class],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyFormDao(): DailyFormDao
    abstract fun waterLogDao(): WaterLogDao
}
