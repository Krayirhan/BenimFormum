package com.krayirhan.benimformum.domain.usecase

import com.krayirhan.benimformum.domain.model.DailyForm
import com.krayirhan.benimformum.domain.model.DailyWaterTotal
import com.krayirhan.benimformum.domain.model.WeeklySummary
import com.krayirhan.benimformum.domain.model.WeeklySummaryItem
import com.krayirhan.benimformum.domain.model.WeeklySummaryTone
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetWeeklySummaryUseCase @Inject constructor(
    private val getHistoryDailyFormsUseCase: GetHistoryDailyFormsUseCase,
    private val getDailyWaterTotalsUseCase: GetDailyWaterTotalsUseCase,
    private val observeAppPreferencesUseCase: ObserveAppPreferencesUseCase
) {
    operator fun invoke(): Flow<WeeklySummary> {
        return combine(
            getHistoryDailyFormsUseCase(7),
            getDailyWaterTotalsUseCase(7),
            observeAppPreferencesUseCase()
        ) { forms, waterTotals, preferences ->
            if (forms.isEmpty() && waterTotals.isEmpty()) {
                return@combine WeeklySummary(
                    items = emptyList(),
                    hasData = false
                )
            }

            val sortedForms = forms.sortedBy { it.date }
            val recordedDays = (forms.map { it.date } + waterTotals.map { it.date }).toSet().size
            val waterGoalMl = preferences.waterGoalMl.coerceAtLeast(1)
            val waterTargetDays = waterTotals.count { it.totalMl >= waterGoalMl }
            val items = buildList {
                add(recordingCoverageItem(recordedDays, forms, waterTotals))
                add(waterItem(waterTotals, waterGoalMl, waterTargetDays))
                add(energyItem(sortedForms))
                add(sleepItem(sortedForms))
                add(nightSnackItem(sortedForms))
                weightItem(sortedForms)?.let(::add)
                if (recordedDays < MIN_DAYS_FOR_PATTERN) {
                    add(dataScarcityItem(recordedDays))
                }
            }

            WeeklySummary(
                items = items,
                hasData = true,
                headline = weeklyHeadline(recordedDays, waterTargetDays),
                helperText = weeklyHelper(recordedDays, waterTargetDays, waterGoalMl),
                recordedDays = recordedDays
            )
        }
    }

    private fun recordingCoverageItem(
        recordedDays: Int,
        forms: List<DailyForm>,
        waterTotals: List<DailyWaterTotal>
    ): WeeklySummaryItem {
        val description = when {
            recordedDays < MIN_DAYS_FOR_PATTERN ->
                "Henüz az veri var; bu özet yalnızca küçük sinyalleri gösterir."

            forms.isEmpty() ->
                "Bu hafta yalnızca su kayıtların var; form alanları için yorum yapılmadı."

            waterTotals.isEmpty() ->
                "Bu hafta günlük form kayıtların var, fakat su kaydı girilmedi."

            else ->
                "Form veya su eklediğin günler haftalık kapsam olarak sayıldı."
        }

        return WeeklySummaryItem(
            title = "Kayıt kapsamı",
            description = description,
            metric = "$recordedDays / 7",
            tone = WeeklySummaryTone.NEUTRAL
        )
    }

    private fun waterItem(
        waterTotals: List<DailyWaterTotal>,
        waterGoalMl: Int,
        targetDays: Int
    ): WeeklySummaryItem {
        val avgWater = waterTotals.map { it.totalMl }.averageOrNull()?.toInt() ?: 0
        val description = when {
            waterTotals.isEmpty() ->
                "Bu hafta su kaydı yok. Özet için birkaç günlük kayıt yeterli olur."

            targetDays == 0 ->
                "Kayıtlı günlerde su miktarın hedefinin altında kalmış. İstersen hedefini veya günlük ritmini Ayarlar’dan gözden geçirebilirsin."

            targetDays == waterTotals.size ->
                "Su kaydı olan tüm günlerde günlük hedefinin üzerindesin."

            else ->
                "Günlük su hedefinin üzerinde olduğun $targetDays gün var."
        }

        return WeeklySummaryItem(
            title = "Su (günlük hedef)",
            description = description,
            metric = if (waterTotals.isEmpty()) null else "$avgWater ml",
            tone = WeeklySummaryTone.WATER
        )
    }

    private fun energyItem(forms: List<DailyForm>): WeeklySummaryItem {
        val scores = forms.mapNotNull { it.energyScore }
        val average = scores.averageOrNull()
        val description = when {
            scores.isEmpty() ->
                "Bu hafta enerji skoru kaydedilmedi."

            scores.size < MIN_DAYS_FOR_PATTERN ->
                "Enerji için veri az; ortalamayı yalnızca kısa bir not olarak görmek daha doğru."

            average != null && average >= 7.0 ->
                "Enerji kayıtların haftanın genelinde yüksek tarafta kalmış."

            average != null && average <= 4.0 ->
                "Enerji kayıtların bu hafta daha alçak bantta; burada yalnızca gözlem olarak gösterilir."

            else ->
                "Enerji kayıtların orta bantta, büyük bir uç göstermeden ilerlemiş."
        }

        return WeeklySummaryItem(
            title = "Enerji",
            description = description,
            metric = average?.let { "${formatOneDecimal(it)} / 10" },
            tone = WeeklySummaryTone.ENERGY
        )
    }

    private fun sleepItem(forms: List<DailyForm>): WeeklySummaryItem {
        val scores = forms.mapNotNull { it.sleepQuality }
        val average = scores.averageOrNull()
        val description = when {
            scores.isEmpty() ->
                "Bu hafta uyku kalitesi işaretlenmedi."

            scores.size < MIN_DAYS_FOR_PATTERN ->
                "Uyku için birkaç kayıt var; trend demek için henüz erken."

            average != null && average >= 4.0 ->
                "Uyku kayıtların haftanın genelinde güçlü tarafta görünüyor."

            average != null && average <= 2.0 ->
                "Uyku kayıtların düşük tarafta; bu sadece haftalık bir işaret olarak tutulur."

            else ->
                "Uyku kayıtların orta bantta ilerlemiş."
        }

        return WeeklySummaryItem(
            title = "Uyku",
            description = description,
            metric = average?.let { "${formatOneDecimal(it)} / 5" },
            tone = WeeklySummaryTone.SLEEP
        )
    }

    private fun nightSnackItem(forms: List<DailyForm>): WeeklySummaryItem {
        val values = forms.mapNotNull { it.nightSnackDone }
        val snackDays = values.count { it }
        val description = when {
            values.isEmpty() ->
                "Bu hafta gece atıştırması alanı seçilmedi."

            snackDays == 0 ->
                "İşaretlenen günlerde gece atıştırması kaydı yok."

            else ->
                "Gece atıştırması $snackDays gün işaretlendi; bu yalnızca örüntü olarak gösterilir."
        }

        return WeeklySummaryItem(
            title = "Gece atıştırması",
            description = description,
            metric = if (values.isEmpty()) null else "$snackDays / ${values.size}",
            tone = WeeklySummaryTone.FOOD
        )
    }

    private fun weightItem(forms: List<DailyForm>): WeeklySummaryItem? {
        val weights = forms.mapNotNull { form ->
            form.weight?.let { form.date to it }
        }
        if (weights.isEmpty()) return null
        if (weights.size == 1) {
            return WeeklySummaryItem(
                title = "Kilo kaydı",
                description = "Bu hafta tek kilo kaydı var; değişim yorumu için en az iki kayıt gerekir.",
                metric = "1 kayıt",
                tone = WeeklySummaryTone.WEIGHT
            )
        }

        val delta = weights.last().second - weights.first().second
        val description = if (kotlin.math.abs(delta) < 0.05) {
            "İlk ve son kilo kaydı arasında belirgin fark görünmüyor."
        } else {
            "İlk ve son kilo kaydı arasındaki fark sakin bir trend notu olarak gösterilir."
        }

        return WeeklySummaryItem(
            title = "Kilo eğilimi",
            description = description,
            metric = "${if (delta > 0) "+" else ""}${formatOneDecimal(delta)} kg",
            tone = WeeklySummaryTone.WEIGHT
        )
    }

    private fun dataScarcityItem(recordedDays: Int): WeeklySummaryItem {
        return WeeklySummaryItem(
            title = "Veri notu",
            description = "Grafik ve örüntüler için birkaç günlük kayıt yeterli olur; şu an $recordedDays gün üzerinden özetleniyor.",
            metric = null,
            tone = WeeklySummaryTone.NEUTRAL
        )
    }

    private fun weeklyHeadline(recordedDays: Int, waterTargetDays: Int): String {
        return when {
            recordedDays < MIN_DAYS_FOR_PATTERN -> "Veri birikmeye başlıyor"
            waterTargetDays >= 4 -> "Su hedefi güçlü görünmüş"
            recordedDays >= 5 -> "Haftanın ritmi görünür hale geldi"
            else -> "Haftanın kısa özeti hazır"
        }
    }

    private fun weeklyHelper(recordedDays: Int, waterTargetDays: Int, waterGoalMl: Int): String {
        return when {
            recordedDays < MIN_DAYS_FOR_PATTERN ->
                "Bu özet $recordedDays günlük veriye dayanıyor; bu yüzden yorumlar kısa tutuldu."

            waterTargetDays > 0 ->
                "$waterGoalMl ml su hedefin $waterTargetDays gün karşılandı. Diğer metrikler yargısız gözlem olarak listelenir."

            else ->
                "Bu özet son 7 gündeki yerel kayıtlarından oluşturuldu; tıbbi tavsiye içermez."
        }
    }

    private fun List<Int>.averageOrNull(): Double? {
        return if (isEmpty()) null else average()
    }

    private fun formatOneDecimal(value: Double): String {
        return String.format(Locale.US, "%.1f", value)
    }

    private companion object {
        const val MIN_DAYS_FOR_PATTERN = 3
    }
}
