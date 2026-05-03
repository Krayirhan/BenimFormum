package com.krayirhan.benimformum.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.krayirhan.benimformum.data.local.database.AppDatabase
import com.krayirhan.benimformum.data.local.entity.WaterLogEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WaterLogDaoAndroidTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: WaterLogDao

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.waterLogDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun sumsWaterPerDate(): Unit = runBlocking {
        val date = "2025-05-02"
        dao.insertWaterLog(
            WaterLogEntity(date = date, amountMl = 250, timestamp = 1L, source = "manual")
        )
        dao.insertWaterLog(
            WaterLogEntity(date = date, amountMl = 250, timestamp = 2L, source = "manual")
        )
        val total = dao.getTotalWaterByDateAsFlow(date).first()
        assertEquals(500, total)
    }

    @Test
    fun dailyTotalsBetweenDatesGroupsByDay(): Unit = runBlocking {
        dao.insertWaterLog(
            WaterLogEntity(date = "2025-05-01", amountMl = 100, timestamp = 1L, source = "manual")
        )
        dao.insertWaterLog(
            WaterLogEntity(date = "2025-05-02", amountMl = 200, timestamp = 2L, source = "manual")
        )
        val rows = dao.getDailyWaterTotalsBetweenDatesAsFlow(
            startDate = "2025-05-01",
            endDate = "2025-05-02"
        ).first()
        assertEquals(2, rows.size)
        assertEquals("2025-05-02", rows[0].date)
        assertEquals(200, rows[0].totalMl)
        assertEquals("2025-05-01", rows[1].date)
        assertEquals(100, rows[1].totalMl)
    }
}
