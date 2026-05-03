package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.model.DailyForm
import com.krayirhan.benimformum.domain.repository.DailyFormRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetHistoryDailyFormsUseCase @Inject constructor(
    private val dailyFormRepository: DailyFormRepository
) {
    operator fun invoke(days: Int): Flow<List<DailyForm>> {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays((days - 1).toLong())
        return dailyFormRepository.observeDailyFormsBetween(
            startDate = startDate.toString(),
            endDate = endDate.toString()
        )
    }
}
