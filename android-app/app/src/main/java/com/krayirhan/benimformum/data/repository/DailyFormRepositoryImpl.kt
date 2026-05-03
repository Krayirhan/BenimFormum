package com.krayirhan.benimformum.data.repository

import com.krayirhan.benimformum.data.local.dao.DailyFormDao
import com.krayirhan.benimformum.data.local.entity.DailyFormEntity
import com.krayirhan.benimformum.domain.model.DailyForm
import com.krayirhan.benimformum.domain.repository.DailyFormRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DailyFormRepositoryImpl @Inject constructor(
    private val dailyFormDao: DailyFormDao
) : DailyFormRepository {

    override fun observeDailyForm(date: String): Flow<DailyForm?> {
        return dailyFormDao.getDailyFormByDateAsFlow(date).map { it?.toDomain() }
    }

    override suspend fun getDailyForm(date: String): DailyForm? {
        return dailyFormDao.getDailyFormByDate(date)?.toDomain()
    }

    override fun observeDailyFormsBetween(startDate: String, endDate: String): Flow<List<DailyForm>> {
        return dailyFormDao
            .getDailyFormsBetweenDatesAsFlow(startDate, endDate)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getAllDailyForms(): List<DailyForm> {
        return dailyFormDao.getAllDailyForms().map { it.toDomain() }
    }

    override suspend fun saveDailyForm(form: DailyForm) {
        dailyFormDao.insertOrUpdateDailyForm(form.toEntity())
    }
}

private fun DailyFormEntity.toDomain(): DailyForm {
    return DailyForm(
        date = date,
        weight = weight,
        sleepQuality = sleepQuality,
        energyScore = energyScore,
        moodScore = moodScore,
        nightSnackDone = nightSnackDone,
        note = note,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun DailyForm.toEntity(): DailyFormEntity {
    return DailyFormEntity(
        date = date,
        weight = weight,
        sleepQuality = sleepQuality,
        energyScore = energyScore,
        moodScore = moodScore,
        nightSnackDone = nightSnackDone,
        note = note,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
