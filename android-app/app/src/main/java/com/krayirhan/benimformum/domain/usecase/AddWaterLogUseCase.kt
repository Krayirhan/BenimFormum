package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.model.WaterLog
import com.krayirhan.benimformum.domain.repository.WaterLogRepository
import java.time.LocalDate
import javax.inject.Inject

class AddWaterLogUseCase @Inject constructor(
    private val waterLogRepository: WaterLogRepository
) {
    suspend operator fun invoke(amountMl: Int = 250): Result<Unit> {
        if (amountMl <= 0) {
            return Result.failure(IllegalArgumentException("Su miktarı pozitif olmalıdır."))
        }
        waterLogRepository.addWaterLog(
            WaterLog(
                date = LocalDate.now().toString(),
                amountMl = amountMl,
                timestamp = System.currentTimeMillis()
            )
        )
        return Result.success(Unit)
    }
}
