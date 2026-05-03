package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.model.AppPreferences
import com.krayirhan.benimformum.domain.model.DailyForm
import com.krayirhan.benimformum.domain.model.DailyWaterTotal
import com.krayirhan.benimformum.domain.model.WeeklySummaryTone
import com.krayirhan.benimformum.testutil.FakeAppSettingsRepository
import com.krayirhan.benimformum.testutil.FakeDailyFormRepository
import com.krayirhan.benimformum.testutil.FakeWaterLogRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GetWeeklySummaryUseCaseTest {

    @Test
    fun `empty forms and water returns no data`() = runTest {
        val daily = FakeDailyFormRepository()
        val water = FakeWaterLogRepository()
        daily.emitBetween(emptyList())
        water.emitTotals(emptyList())
        val useCase = createUseCase(daily, water)
        val summary = useCase().first()
        assertFalse(summary.hasData)
        assertTrue(summary.items.isEmpty())
    }

    @Test
    fun `with sparse form data returns contextual items and hasData`() = runTest {
        val daily = FakeDailyFormRepository()
        val water = FakeWaterLogRepository()
        val form = dailyForm(
            date = "2024-01-10",
            weight = 70.0,
            sleepQuality = 4,
            energyScore = 6,
            moodScore = 3,
            nightSnackDone = false
        )
        daily.emitBetween(listOf(form))
        water.emitTotals(listOf(DailyWaterTotal("2024-01-10", 500)))

        val summary = createUseCase(daily, water)().first()

        assertTrue(summary.hasData)
        assertEquals("Veri birikmeye başlıyor", summary.headline)
        assertEquals(1, summary.recordedDays)
        assertTrue(summary.items.size >= 6)
        assertEquals("1 / 7", summary.items[0].metric)
        assertEquals("500 ml", summary.items[1].metric)
        assertEquals(WeeklySummaryTone.WATER, summary.items[1].tone)
        assertTrue(summary.items.any { it.title == "Veri notu" })
    }

    @Test
    fun `water target uses app preferences`() = runTest {
        val daily = FakeDailyFormRepository()
        val water = FakeWaterLogRepository()
        daily.emitBetween(
            listOf(
                dailyForm(date = "2024-01-08", energyScore = 7),
                dailyForm(date = "2024-01-09", energyScore = 8),
                dailyForm(date = "2024-01-10", energyScore = 6)
            )
        )
        water.emitTotals(
            listOf(
                DailyWaterTotal("2024-01-08", 1500),
                DailyWaterTotal("2024-01-09", 1800),
                DailyWaterTotal("2024-01-10", 1000)
            )
        )

        val summary = createUseCase(
            daily = daily,
            water = water,
            preferences = AppPreferences(waterGoalMl = 1500)
        )().first()

        assertTrue(summary.hasData)
        assertTrue(summary.helperText.contains("1500 ml"))
        assertTrue(summary.helperText.contains("2 gün"))
    }

    private fun createUseCase(
        daily: FakeDailyFormRepository,
        water: FakeWaterLogRepository,
        preferences: AppPreferences = AppPreferences()
    ): GetWeeklySummaryUseCase {
        return GetWeeklySummaryUseCase(
            GetHistoryDailyFormsUseCase(daily),
            GetDailyWaterTotalsUseCase(water),
            ObserveAppPreferencesUseCase(FakeAppSettingsRepository(preferences))
        )
    }

    private fun dailyForm(
        date: String,
        weight: Double? = null,
        sleepQuality: Int? = null,
        energyScore: Int? = null,
        moodScore: Int? = null,
        nightSnackDone: Boolean? = null
    ): DailyForm {
        return DailyForm(
            date = date,
            weight = weight,
            sleepQuality = sleepQuality,
            energyScore = energyScore,
            moodScore = moodScore,
            nightSnackDone = nightSnackDone,
            note = null,
            createdAt = 1L,
            updatedAt = 2L
        )
    }
}
