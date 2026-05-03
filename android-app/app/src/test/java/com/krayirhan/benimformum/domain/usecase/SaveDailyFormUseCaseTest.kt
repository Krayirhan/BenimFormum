package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.model.DailyForm
import com.krayirhan.benimformum.testutil.FakeDailyFormRepository
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveDailyFormUseCaseTest {

    private val today: String = LocalDate.now().toString()

    @Test
    fun `saves valid form`() = runBlocking {
        val repo = FakeDailyFormRepository()
        val useCase = SaveDailyFormUseCase(repo)
        val result = useCase(
            weightInput = "70.5",
            sleepQualityInput = "4",
            energyScoreInput = "6",
            moodScoreInput = "3",
            nightSnackDone = false,
            noteInput = "  iyi  "
        )
        assertTrue(result.isSuccess)
        val saved = repo.lastSaved!!
        assertEquals(today, saved.date)
        assertEquals(70.5, saved.weight!!, 0.0001)
        assertEquals(4, saved.sleepQuality)
        assertEquals(6, saved.energyScore)
        assertEquals(3, saved.moodScore)
        assertEquals(false, saved.nightSnackDone)
        assertEquals("iyi", saved.note)
    }

    @Test
    fun `comma decimal separator works`() = runBlocking {
        val repo = FakeDailyFormRepository()
        val useCase = SaveDailyFormUseCase(repo)
        val result = useCase(
            weightInput = "68,2",
            sleepQualityInput = "",
            energyScoreInput = "5",
            moodScoreInput = "4",
            nightSnackDone = true,
            noteInput = ""
        )
        assertTrue(result.isSuccess)
        assertEquals(68.2, repo.lastSaved!!.weight!!, 0.0001)
        assertEquals(null, repo.lastSaved!!.sleepQuality)
    }

    @Test
    fun `energy mood and night snack can remain unentered`() = runBlocking {
        val repo = FakeDailyFormRepository()
        val useCase = SaveDailyFormUseCase(repo)
        val result = useCase(
            weightInput = "68",
            sleepQualityInput = "",
            energyScoreInput = "",
            moodScoreInput = "",
            nightSnackDone = null,
            noteInput = ""
        )
        assertTrue(result.isSuccess)
        assertEquals(null, repo.lastSaved!!.energyScore)
        assertEquals(null, repo.lastSaved!!.moodScore)
        assertEquals(null, repo.lastSaved!!.nightSnackDone)
    }

    @Test
    fun `failure when no field is entered`() = runBlocking {
        val repo = FakeDailyFormRepository()
        val useCase = SaveDailyFormUseCase(repo)
        val result = useCase(
            weightInput = "",
            sleepQualityInput = "",
            energyScoreInput = "",
            moodScoreInput = "",
            nightSnackDone = null,
            noteInput = ""
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `failure when weight invalid`() = runBlocking {
        val repo = FakeDailyFormRepository()
        val useCase = SaveDailyFormUseCase(repo)
        val result = useCase(
            weightInput = "abc",
            sleepQualityInput = "",
            energyScoreInput = "5",
            moodScoreInput = "4",
            nightSnackDone = false,
            noteInput = ""
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `failure when energy out of range`() = runBlocking {
        val repo = FakeDailyFormRepository()
        val useCase = SaveDailyFormUseCase(repo)
        val result = useCase(
            weightInput = "",
            sleepQualityInput = "",
            energyScoreInput = "11",
            moodScoreInput = "4",
            nightSnackDone = false,
            noteInput = ""
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `preserves createdAt when updating`() = runBlocking {
        val existingCreated = 1000L
        val repo = FakeDailyFormRepository()
        repo.seed(
            today,
            DailyForm(
                date = today,
                weight = 70.0,
                sleepQuality = 3,
                energyScore = 5,
                moodScore = 3,
                nightSnackDone = false,
                note = null,
                createdAt = existingCreated,
                updatedAt = 2000L
            )
        )
        val useCase = SaveDailyFormUseCase(repo)
        useCase(
            weightInput = "71",
            sleepQualityInput = "3",
            energyScoreInput = "5",
            moodScoreInput = "3",
            nightSnackDone = false,
            noteInput = ""
        )
        assertEquals(existingCreated, repo.lastSaved!!.createdAt)
    }
}
