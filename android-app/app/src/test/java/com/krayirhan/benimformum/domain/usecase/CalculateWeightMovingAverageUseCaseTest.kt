package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.model.DailyForm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateWeightMovingAverageUseCaseTest {

    private val useCase = CalculateWeightMovingAverageUseCase()

    @Test
    fun `empty list returns empty`() {
        assertTrue(useCase(emptyList()).isEmpty())
    }

    @Test
    fun `less than three weights with data returns empty`() {
        val forms = listOf(
            form("2024-01-01", 70.0),
            form("2024-01-02", 71.0)
        )
        assertTrue(useCase(forms).isEmpty())
    }

    @Test
    fun `three days produces one moving average point`() {
        val forms = listOf(
            form("2024-01-01", 70.0),
            form("2024-01-02", 72.0),
            form("2024-01-03", 74.0)
        )
        val trend = useCase(forms, window = 3)
        assertEquals(1, trend.size)
        assertEquals("2024-01-01", trend[0].date)
        assertEquals(70.0, trend[0].weight, 0.0001)
        assertEquals((70.0 + 72.0 + 74.0) / 3.0, trend[0].movingAverage, 0.0001)
    }

    @Test
    fun `skips entries without weight`() {
        val forms = listOf(
            form("2024-01-01", null),
            form("2024-01-02", 70.0),
            form("2024-01-03", 72.0),
            form("2024-01-04", 74.0)
        )
        val trend = useCase(forms, window = 3)
        assertEquals(1, trend.size)
        assertEquals("2024-01-02", trend[0].date)
    }

    private fun form(date: String, weight: Double?) = DailyForm(
        date = date,
        weight = weight,
        sleepQuality = null,
        energyScore = 5,
        moodScore = 3,
        nightSnackDone = false,
        note = null,
        createdAt = 0,
        updatedAt = 0
    )
}
