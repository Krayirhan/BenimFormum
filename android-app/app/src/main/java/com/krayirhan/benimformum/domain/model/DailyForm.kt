package com.krayirhan.benimformum.domain.model

data class DailyForm(
    val date: String,
    val weight: Double?,
    val sleepQuality: Int?,
    val energyScore: Int?,
    val moodScore: Int?,
    val nightSnackDone: Boolean?,
    val note: String?,
    val createdAt: Long,
    val updatedAt: Long
)
