package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.repository.WaterLogRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetTodayWaterTotalUseCase @Inject constructor(
    private val waterLogRepository: WaterLogRepository
) {
    operator fun invoke(): Flow<Int> {
        return waterLogRepository.getTodayWaterTotal(LocalDate.now().toString())
    }
}
