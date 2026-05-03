package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.repository.AppSettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveOnboardingCompletedUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return appSettingsRepository.observeOnboardingCompleted()
    }
}
