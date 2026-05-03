package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.model.DailyForm
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateFormScoreUseCaseTest {

    private val useCase = CalculateFormScoreUseCase()

    @Test
    fun `null form returns zero`() {
        assertEquals(0, useCase(null, 500))
    }

    @Test
    fun `energy and mood contribute max base`() {
        val form = DailyForm(
            date = "2024-01-01",
            weight = null,
            sleepQuality = null,
            energyScore = 10,
            moodScore = 5,
            nightSnackDone = true,
            note = null,
            createdAt = 0,
            updatedAt = 0
        )
        assertEquals(40 + 30, useCase(form, 0))
    }

    @Test
    fun `sleep adds up to 20`() {
        val form = DailyForm(
            date = "2024-01-01",
            weight = null,
            sleepQuality = 5,
            energyScore = 1,
            moodScore = 1,
            nightSnackDone = true,
            note = null,
            createdAt = 0,
            updatedAt = 0
        )
        assertEquals(4 + 6 + 20, useCase(form, 0))
    }

    @Test
    fun `night snack bonus when false`() {
        val form = DailyForm(
            date = "2024-01-01",
            weight = null,
            sleepQuality = null,
            energyScore = 1,
            moodScore = 1,
            nightSnackDone = false,
            note = null,
            createdAt = 0,
            updatedAt = 0
        )
        assertEquals(4 + 6 + 5, useCase(form, 0))
    }

    @Test
    fun `water tiers`() {
        val form = DailyForm(
            date = "2024-01-01",
            weight = null,
            sleepQuality = null,
            energyScore = 1,
            moodScore = 1,
            nightSnackDone = true,
            note = null,
            createdAt = 0,
            updatedAt = 0
        )
        assertEquals(11, useCase(form, 1))
        assertEquals(13, useCase(form, 1000))
        assertEquals(15, useCase(form, 2000))
    }

    @Test
    fun `unentered energy mood and night snack do not add score`() {
        val form = DailyForm(
            date = "2024-01-01",
            weight = 70.0,
            sleepQuality = null,
            energyScore = null,
            moodScore = null,
            nightSnackDone = null,
            note = null,
            createdAt = 0,
            updatedAt = 0
        )
        assertEquals(0, useCase(form, 0))
    }
}
