package com.krayirhan.benimformum.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.krayirhan.benimformum.data.local.dao.DailyFormDao
import com.krayirhan.benimformum.data.local.dao.WaterLogDao
import com.krayirhan.benimformum.data.local.database.AppDatabase
import com.krayirhan.benimformum.data.local.database.AppDatabaseMigrations
import com.krayirhan.benimformum.data.repository.AppSettingsRepositoryImpl
import com.krayirhan.benimformum.data.repository.DailyFormRepositoryImpl
import com.krayirhan.benimformum.data.repository.WaterLogRepositoryImpl
import com.krayirhan.benimformum.domain.repository.AppSettingsRepository
import com.krayirhan.benimformum.domain.repository.DailyFormRepository
import com.krayirhan.benimformum.domain.repository.WaterLogRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val SETTINGS_NAME = "app_settings"
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_NAME)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.settingsDataStore
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "benim_formum.db"
        )
            .addMigrations(*AppDatabaseMigrations.ALL)
            .build()
    }

    @Provides
    @Singleton
    fun provideDailyFormDao(database: AppDatabase): DailyFormDao {
        return database.dailyFormDao()
    }

    @Provides
    @Singleton
    fun provideWaterLogDao(database: AppDatabase): WaterLogDao {
        return database.waterLogDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAppSettingsRepository(
        impl: AppSettingsRepositoryImpl
    ): AppSettingsRepository

    @Binds
    @Singleton
    abstract fun bindDailyFormRepository(
        impl: DailyFormRepositoryImpl
    ): DailyFormRepository

    @Binds
    @Singleton
    abstract fun bindWaterLogRepository(
        impl: WaterLogRepositoryImpl
    ): WaterLogRepository
}
