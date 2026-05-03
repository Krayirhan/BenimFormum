package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.model.AppPreferences
import com.krayirhan.benimformum.domain.repository.AppSettingsRepository
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository
) {
    suspend operator fun invoke(preferences: AppPreferences = AppPreferences()) {
        appSettingsRepository.saveAppPreferences(preferences)
        appSettingsRepository.setOnboardingCompleted(true)
    }
}
