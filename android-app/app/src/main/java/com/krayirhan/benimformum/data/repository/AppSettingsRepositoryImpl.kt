package com.krayirhan.benimformum.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.krayirhan.benimformum.domain.model.AppPreferences
import com.krayirhan.benimformum.domain.model.ThemePreference
import com.krayirhan.benimformum.domain.model.TrackedMetric
import com.krayirhan.benimformum.domain.repository.AppSettingsRepository
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class AppSettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AppSettingsRepository {

    override fun observeAppPreferences(): Flow<AppPreferences> {
        return safePreferencesFlow().map { preferences ->
            AppPreferences(
                waterGoalMl = preferences[WATER_GOAL_ML]
                    ?.coerceIn(MIN_WATER_GOAL_ML, MAX_WATER_GOAL_ML)
                    ?: AppPreferences.DEFAULT_WATER_GOAL_ML,
                themePreference = preferences[THEME_PREFERENCE].toThemePreference(),
                dynamicColor = preferences[DYNAMIC_COLOR] ?: false,
                trackedMetrics = preferences[TRACKED_METRICS].toTrackedMetrics()
            )
        }
    }

    override fun observeOnboardingCompleted(): Flow<Boolean> {
        return safePreferencesFlow().map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }
    }

    override suspend fun saveAppPreferences(preferences: AppPreferences) {
        dataStore.edit { storedPreferences ->
            storedPreferences[WATER_GOAL_ML] = preferences.waterGoalMl
                .coerceIn(MIN_WATER_GOAL_ML, MAX_WATER_GOAL_ML)
            storedPreferences[THEME_PREFERENCE] = preferences.themePreference.name
            storedPreferences[DYNAMIC_COLOR] = preferences.dynamicColor
            storedPreferences[TRACKED_METRICS] = preferences.trackedMetrics
                .ifEmpty { AppPreferences.DEFAULT_TRACKED_METRICS }
                .map { it.name }
                .toSet()
        }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    private fun safePreferencesFlow(): Flow<Preferences> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
    }

    private fun String?.toThemePreference(): ThemePreference {
        return ThemePreference.values().firstOrNull { it.name == this } ?: ThemePreference.SYSTEM
    }

    private fun Set<String>?.toTrackedMetrics(): Set<TrackedMetric> {
        val metrics = this
            ?.mapNotNull { storedName ->
                TrackedMetric.values().firstOrNull { it.name == storedName }
            }
            ?.toSet()
            .orEmpty()

        return metrics.ifEmpty { AppPreferences.DEFAULT_TRACKED_METRICS }
    }

    private companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val WATER_GOAL_ML = intPreferencesKey("water_goal_ml")
        val THEME_PREFERENCE = stringPreferencesKey("theme_preference")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val TRACKED_METRICS = stringSetPreferencesKey("tracked_metrics")

        const val MIN_WATER_GOAL_ML = 1000
        const val MAX_WATER_GOAL_ML = 4000
    }
}
