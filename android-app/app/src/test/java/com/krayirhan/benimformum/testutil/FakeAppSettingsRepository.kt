package com.krayirhan.benimformum.testutil

import com.krayirhan.benimformum.domain.model.AppPreferences
import com.krayirhan.benimformum.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAppSettingsRepository(
    initialPreferences: AppPreferences = AppPreferences()
) : AppSettingsRepository {

    private val preferencesFlow = MutableStateFlow(initialPreferences)
    private val onboardingCompletedFlow = MutableStateFlow(false)

    override fun observeAppPreferences(): Flow<AppPreferences> {
        return preferencesFlow
    }

    override fun observeOnboardingCompleted(): Flow<Boolean> {
        return onboardingCompletedFlow
    }

    override suspend fun saveAppPreferences(preferences: AppPreferences) {
        preferencesFlow.value = preferences
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        onboardingCompletedFlow.value = completed
    }

    fun emitPreferences(preferences: AppPreferences) {
        preferencesFlow.value = preferences
    }
}
