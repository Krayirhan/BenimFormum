package com.krayirhan.benimformum.testutil

import com.krayirhan.benimformum.domain.model.DailyForm
import com.krayirhan.benimformum.domain.repository.DailyFormRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeDailyFormRepository(
    private val betweenFlow: MutableStateFlow<List<DailyForm>> = MutableStateFlow(emptyList())
) : DailyFormRepository {

    private val stored = mutableMapOf<String, DailyForm>()

    var capturedBetweenStart: String? = null
    var capturedBetweenEnd: String? = null
    var lastSaved: DailyForm? = null

    override fun observeDailyForm(date: String): Flow<DailyForm?> = flowOf(stored[date])

    override fun observeDailyFormsBetween(startDate: String, endDate: String): Flow<List<DailyForm>> {
        capturedBetweenStart = startDate
        capturedBetweenEnd = endDate
        return betweenFlow
    }

    override suspend fun getDailyForm(date: String): DailyForm? = stored[date]

    override suspend fun getAllDailyForms(): List<DailyForm> {
        return stored.values.sortedBy { it.date }
    }

    override suspend fun saveDailyForm(form: DailyForm) {
        lastSaved = form
        stored[form.date] = form
    }

    fun emitBetween(list: List<DailyForm>) {
        betweenFlow.value = list
    }

    fun seed(date: String, form: DailyForm) {
        stored[date] = form
    }
}
