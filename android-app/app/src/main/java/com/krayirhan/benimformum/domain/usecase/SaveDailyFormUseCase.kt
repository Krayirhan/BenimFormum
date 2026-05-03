package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.model.DailyForm
import com.krayirhan.benimformum.domain.repository.DailyFormRepository
import java.time.LocalDate
import javax.inject.Inject

class SaveDailyFormUseCase @Inject constructor(
    private val repository: DailyFormRepository
) {
    suspend operator fun invoke(
        weightInput: String,
        sleepQualityInput: String,
        energyScoreInput: String,
        moodScoreInput: String,
        nightSnackDone: Boolean?,
        noteInput: String
    ): Result<Unit> {
        val now = System.currentTimeMillis()
        val date = LocalDate.now().toString()
        val existing = repository.getDailyForm(date)

        val normalizedWeight = weightInput.trim().replace(",", ".")
        val weight = normalizedWeight.takeIf { it.isNotEmpty() }?.toDoubleOrNull()
            ?: if (weightInput.isBlank()) null else return Result.failure(
                IllegalArgumentException("Kilo alanı için sayısal bir değer girebilirsin.")
            )
        if (weight != null && weight < 0) {
            return Result.failure(IllegalArgumentException("Kilo değeri 0 veya daha büyük olmalı."))
        }

        val sleepQuality = sleepQualityInput.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
            ?: if (sleepQualityInput.isBlank()) null else return Result.failure(
                IllegalArgumentException("Uyku kalitesi 1 ile 5 arasında olmalı.")
            )
        if (sleepQuality != null && sleepQuality !in 1..5) {
            return Result.failure(IllegalArgumentException("Uyku kalitesi 1 ile 5 arasında olmalı."))
        }

        val energyScore = energyScoreInput.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
            ?: if (energyScoreInput.isBlank()) null else return Result.failure(
                IllegalArgumentException("Enerji skoru 1 ile 10 arasında olmalı.")
            )
        if (energyScore != null && energyScore !in 1..10) {
            return Result.failure(IllegalArgumentException("Enerji skoru 1 ile 10 arasında olmalı."))
        }

        val moodScore = moodScoreInput.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
            ?: if (moodScoreInput.isBlank()) null else return Result.failure(
                IllegalArgumentException("Ruh hâli skoru 1 ile 5 arasında olmalı.")
            )
        if (moodScore != null && moodScore !in 1..5) {
            return Result.failure(IllegalArgumentException("Ruh hâli skoru 1 ile 5 arasında olmalı."))
        }

        val note = noteInput.trim().ifEmpty { null }
        val hasAnyEntry = weight != null ||
            sleepQuality != null ||
            energyScore != null ||
            moodScore != null ||
            nightSnackDone != null ||
            note != null
        if (!hasAnyEntry) {
            return Result.failure(
                IllegalArgumentException("Kaydetmek için en az bir alanı doldurabilirsin.")
            )
        }

        val form = DailyForm(
            date = date,
            weight = weight,
            sleepQuality = sleepQuality,
            energyScore = energyScore,
            moodScore = moodScore,
            nightSnackDone = nightSnackDone,
            note = note,
            createdAt = existing?.createdAt ?: now,
            updatedAt = now
        )
        repository.saveDailyForm(form)
        return Result.success(Unit)
    }
}
