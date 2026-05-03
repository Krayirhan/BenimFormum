package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.model.DailyForm
import com.krayirhan.benimformum.domain.repository.DailyFormRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetTodayDailyFormUseCase @Inject constructor(
    private val repository: DailyFormRepository
) {
    operator fun invoke(): Flow<DailyForm?> {
        return repository.observeDailyForm(LocalDate.now().toString())
    }
}
