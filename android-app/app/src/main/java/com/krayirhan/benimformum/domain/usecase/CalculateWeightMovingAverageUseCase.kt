package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.model.DailyForm
import javax.inject.Inject

data class WeightTrendPoint(
    val date: String,
    val weight: Double,
    val movingAverage: Double
)

class CalculateWeightMovingAverageUseCase @Inject constructor() {
    operator fun invoke(forms: List<DailyForm>, window: Int = 3): List<WeightTrendPoint> {
        if (window <= 0) return emptyList()

        val weighted = forms
            .asReversed()
            .mapNotNull { form ->
                val weight = form.weight ?: return@mapNotNull null
                form.date to weight
            }

        if (weighted.size < window) return emptyList()

        val result = mutableListOf<WeightTrendPoint>()
        for (index in weighted.indices) {
            val from = (index - window + 1).coerceAtLeast(0)
            val slice = weighted.subList(from, index + 1)
            if (slice.size < window) continue
            val avg = slice.map { it.second }.average()
            result += WeightTrendPoint(
                date = weighted[index].first,
                weight = weighted[index].second,
                movingAverage = avg
            )
        }
        return result
    }
}
