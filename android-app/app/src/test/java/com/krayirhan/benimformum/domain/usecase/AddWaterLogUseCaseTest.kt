package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.testutil.FakeWaterLogRepository
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AddWaterLogUseCaseTest {

    @Test
    fun `adds water with default 250ml`() = runBlocking {
        val repo = FakeWaterLogRepository()
        val useCase = AddWaterLogUseCase(repo)
        val result = useCase()
        assertTrue(result.isSuccess)
        assertEquals(1, repo.addedLogs.size)
        assertEquals(250, repo.addedLogs[0].amountMl)
        assertEquals(LocalDate.now().toString(), repo.addedLogs[0].date)
    }

    @Test
    fun `fails for non positive amount`() = runBlocking {
        val repo = FakeWaterLogRepository()
        val useCase = AddWaterLogUseCase(repo)
        val result = useCase(amountMl = 0)
        assertTrue(result.isFailure)
        assertTrue(repo.addedLogs.isEmpty())
    }
}
