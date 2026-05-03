package com.krayirhan.benimformum.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_form")
data class DailyFormEntity(
    @PrimaryKey val date: String,
    val weight: Double?,
    val sleepQuality: Int?,
    val energyScore: Int?,
    val moodScore: Int?,
    val nightSnackDone: Boolean?,
    val note: String?,
    val createdAt: Long,
    val updatedAt: Long
)
