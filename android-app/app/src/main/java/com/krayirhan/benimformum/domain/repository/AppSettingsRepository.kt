package com.krayirhan.benimformum.domain.repository

import com.krayirhan.benimformum.domain.model.AppPreferences
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun observeAppPreferences(): Flow<AppPreferences>
    fun observeOnboardingCompleted(): Flow<Boolean>
    suspend fun saveAppPreferences(preferences: AppPreferences)
    suspend fun setOnboardingCompleted(completed: Boolean)
}
