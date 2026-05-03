package com.krayirhan.benimformum.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_log")
data class WaterLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val date: String,
    val amountMl: Int,
    val timestamp: Long,
    val source: String
)
