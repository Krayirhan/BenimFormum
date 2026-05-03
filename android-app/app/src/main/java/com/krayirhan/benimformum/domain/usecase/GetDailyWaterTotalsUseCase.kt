package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.model.DailyWaterTotal
import com.krayirhan.benimformum.domain.repository.WaterLogRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetDailyWaterTotalsUseCase @Inject constructor(
    private val waterLogRepository: WaterLogRepository
) {
    operator fun invoke(days: Int): Flow<List<DailyWaterTotal>> {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays((days - 1).toLong())
        return waterLogRepository.getDailyWaterTotalsBetween(
            startDate = startDate.toString(),
            endDate = endDate.toString()
        )
    }
}
