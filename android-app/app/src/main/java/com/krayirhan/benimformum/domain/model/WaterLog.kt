package com.krayirhan.benimformum.domain.model

data class WaterLog(
    val id: Long = 0L,
    val date: String,
    val amountMl: Int,
    val timestamp: Long,
    val source: String = "manual"
)
