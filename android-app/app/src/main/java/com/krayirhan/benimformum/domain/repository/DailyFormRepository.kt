package com.krayirhan.benimformum.domain.repository

import com.krayirhan.benimformum.domain.model.DailyForm
import kotlinx.coroutines.flow.Flow

interface DailyFormRepository {
    fun observeDailyForm(date: String): Flow<DailyForm?>
    fun observeDailyFormsBetween(startDate: String, endDate: String): Flow<List<DailyForm>>
    suspend fun getDailyForm(date: String): DailyForm?
    suspend fun getAllDailyForms(): List<DailyForm>
    suspend fun saveDailyForm(form: DailyForm)
}
