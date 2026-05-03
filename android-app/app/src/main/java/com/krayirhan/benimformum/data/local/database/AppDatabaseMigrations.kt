package com.krayirhan.benimformum.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object AppDatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
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
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `daily_form_new` (
                    `date` TEXT NOT NULL,
                    `weight` REAL,
                    `sleepQuality` INTEGER,
                    `energyScore` INTEGER,
                    `moodScore` INTEGER,
                    `nightSnackDone` INTEGER,
                    `note` TEXT,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`date`)
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO `daily_form_new` (
                    `date`,
                    `weight`,
                    `sleepQuality`,
                    `energyScore`,
                    `moodScore`,
                    `nightSnackDone`,
                    `note`,
                    `createdAt`,
                    `updatedAt`
                )
                SELECT
                    `date`,
                    `weight`,
                    `sleepQuality`,
                    `energyScore`,
                    `moodScore`,
                    `nightSnackDone`,
                    `note`,
                    `createdAt`,
                    `updatedAt`
                FROM `daily_form`
                """.trimIndent()
            )
            db.execSQL("DROP TABLE `daily_form`")
            db.execSQL("ALTER TABLE `daily_form_new` RENAME TO `daily_form`")
        }
    }

    val ALL = arrayOf(MIGRATION_1_2, MIGRATION_2_3)
}
