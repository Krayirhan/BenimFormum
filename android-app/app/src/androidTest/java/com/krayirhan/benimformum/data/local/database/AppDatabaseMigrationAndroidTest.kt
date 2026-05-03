package com.krayirhan.benimformum.data.local.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationAndroidTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @After
    fun tearDown() {
        context.deleteDatabase(TEST_DATABASE)
    }

    @Test
    fun migrate1To3_preservesDailyFormAndCreatesWaterLogTable() {
        context.deleteDatabase(TEST_DATABASE)
        createVersion1Database()

        val database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            TEST_DATABASE
        )
            .addMigrations(
                AppDatabaseMigrations.MIGRATION_1_2,
                AppDatabaseMigrations.MIGRATION_2_3
            )
            .build()

        try {
            val db = database.openHelper.writableDatabase
            db.query("SELECT date, weight, energyScore FROM daily_form WHERE date = '2026-05-01'")
                .use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals("2026-05-01", cursor.getString(0))
                    assertEquals(72.5, cursor.getDouble(1), 0.0)
                    assertEquals(7, cursor.getInt(2))
                }

            db.query("SELECT COUNT(*) FROM water_log").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(0, cursor.getInt(0))
            }
        } finally {
            database.close()
        }
    }

    @Test
    fun migrate2To3_keepsExistingValuesAndAllowsNullableScores() {
        context.deleteDatabase(TEST_DATABASE)
        createVersion2Database()

        val database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            TEST_DATABASE
        )
            .addMigrations(AppDatabaseMigrations.MIGRATION_2_3)
            .build()

        try {
            val db = database.openHelper.writableDatabase
            db.query("SELECT date, energyScore, moodScore, nightSnackDone FROM daily_form WHERE date = '2026-05-02'")
                .use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals("2026-05-02", cursor.getString(0))
                    assertEquals(6, cursor.getInt(1))
                    assertEquals(4, cursor.getInt(2))
                    assertEquals(1, cursor.getInt(3))
                }

            db.execSQL(
                """
                INSERT INTO `daily_form` (
                    `date`,
                    `weight`,
                    `sleepQuality`,
                    `energyScore`,
                    `moodScore`,
                    `nightSnackDone`,
                    `note`,
                    `createdAt`,
                    `updatedAt`
                ) VALUES (
                    '2026-05-03',
                    NULL,
                    NULL,
                    NULL,
                    NULL,
                    NULL,
                    'partial',
                    3000,
                    3000
                )
                """.trimIndent()
            )
            db.query("SELECT energyScore, moodScore, nightSnackDone FROM daily_form WHERE date = '2026-05-03'")
                .use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertTrue(cursor.isNull(0))
                    assertTrue(cursor.isNull(1))
                    assertTrue(cursor.isNull(2))
                }
        } finally {
            database.close()
        }
    }

    private fun createVersion1Database() {
        val dbFile = context.getDatabasePath(TEST_DATABASE)
        dbFile.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(dbFile, null).use { db ->
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `daily_form` (
                    `date` TEXT NOT NULL,
                    `weight` REAL,
                    `sleepQuality` INTEGER,
                    `energyScore` INTEGER NOT NULL,
                    `moodScore` INTEGER NOT NULL,
                    `nightSnackDone` INTEGER NOT NULL,
                    `note` TEXT,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`date`)
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO `daily_form` (
                    `date`,
                    `weight`,
                    `sleepQuality`,
                    `energyScore`,
                    `moodScore`,
                    `nightSnackDone`,
                    `note`,
                    `createdAt`,
                    `updatedAt`
                ) VALUES (
                    '2026-05-01',
                    72.5,
                    4,
                    7,
                    3,
                    0,
                    'migration check',
                    1000,
                    2000
                )
                """.trimIndent()
            )
            db.version = 1
        }
    }

    private fun createVersion2Database() {
        val dbFile = context.getDatabasePath(TEST_DATABASE)
        dbFile.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(dbFile, null).use { db ->
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `daily_form` (
                    `date` TEXT NOT NULL,
                    `weight` REAL,
                    `sleepQuality` INTEGER,
                    `energyScore` INTEGER NOT NULL,
                    `moodScore` INTEGER NOT NULL,
                    `nightSnackDone` INTEGER NOT NULL,
                    `note` TEXT,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`date`)
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `water_log` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `date` TEXT NOT NULL,
                    `amountMl` INTEGER NOT NULL,
                    `timestamp` INTEGER NOT NULL,
                    `source` TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO `daily_form` (
                    `date`,
                    `weight`,
                    `sleepQuality`,
                    `energyScore`,
                    `moodScore`,
                    `nightSnackDone`,
                    `note`,
                    `createdAt`,
                    `updatedAt`
                ) VALUES (
                    '2026-05-02',
                    71.0,
                    4,
                    6,
                    4,
                    1,
                    'version two',
                    1000,
                    2000
                )
                """.trimIndent()
            )
            db.version = 2
        }
    }

    private companion object {
        const val TEST_DATABASE = "migration-test.db"
    }
}
