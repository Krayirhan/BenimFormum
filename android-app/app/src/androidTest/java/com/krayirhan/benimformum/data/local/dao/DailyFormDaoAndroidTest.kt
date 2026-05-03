package com.krayirhan.benimformum.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.krayirhan.benimformum.data.local.database.AppDatabase
import com.krayirhan.benimformum.data.local.entity.DailyFormEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DailyFormDaoAndroidTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: DailyFormDao

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.dailyFormDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertUpdateAndReadDailyForm(): Unit = runBlocking {
        val entity = DailyFormEntity(
            date = "2025-05-02",
            weight = 72.5,
            sleepQuality = 4,
            energyScore = 7,
            moodScore = 4,
            nightSnackDone = false,
            note = "test",
            createdAt = 100L,
            updatedAt = 200L
        )
        dao.insertOrUpdateDailyForm(entity)
        val loaded = dao.getDailyFormByDate("2025-05-02")
        assertEquals(72.5, loaded!!.weight!!, 0.001)
        assertEquals(7, loaded.energyScore)

        val flowValue = dao.getDailyFormByDateAsFlow("2025-05-02").first()
        assertEquals("test", flowValue!!.note)
    }

    @Test
    fun getDailyFormsBetweenDatesOrdersDescending(): Unit = runBlocking {
        listOf("2025-05-01", "2025-05-03", "2025-05-02").forEach { date ->
            dao.insertOrUpdateDailyForm(
                DailyFormEntity(
                    date = date,
                    weight = 70.0,
                    sleepQuality = null,
                    energyScore = 5,
                    moodScore = 3,
                    nightSnackDone = false,
                    note = null,
                    createdAt = 1L,
                    updatedAt = 1L
                )
            )
        }
        val list = dao.getDailyFormsBetweenDatesAsFlow(
            startDate = "2025-05-01",
            endDate = "2025-05-03"
        ).first()
        assertEquals(3, list.size)
        assertEquals("2025-05-03", list[0].date)
        assertEquals("2025-05-02", list[1].date)
    }

    @Test
    fun missingDateReturnsNull(): Unit = runBlocking {
        assertNull(dao.getDailyFormByDate("2099-01-01"))
    }
}
