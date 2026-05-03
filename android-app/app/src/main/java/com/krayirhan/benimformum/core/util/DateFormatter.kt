package com.krayirhan.benimformum.core.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val turkish: Locale = Locale.forLanguageTag("tr-TR")
private val isoFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
private val longDayFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMMM", turkish)
private val shortDayFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM", turkish)

object AppDateFormatter {

    fun friendlyDate(isoDate: String): String {
        if (isoDate.isBlank()) return ""
        val date = parseOrNull(isoDate) ?: return isoDate
        val today = LocalDate.now()
        val dayLabel = when (date) {
            today -> "Bugün"
            today.minusDays(1) -> "Dün"
            today.plusDays(1) -> "Yarın"
            else -> date.dayOfWeek.getDisplayName(TextStyle.FULL, turkish)
                .replaceFirstChar { it.titlecase(turkish) }
        }
        return "$dayLabel, ${date.format(longDayFormatter)}"
    }

    fun shortDate(isoDate: String): String {
        if (isoDate.isBlank()) return ""
        val date = parseOrNull(isoDate) ?: return isoDate
        val today = LocalDate.now()
        return when (date) {
            today -> "Bugün"
            today.minusDays(1) -> "Dün"
            else -> date.format(shortDayFormatter)
        }
    }

    fun greeting(now: LocalDate = LocalDate.now()): String {
        val hour = java.time.LocalTime.now().hour
        return when (hour) {
            in 5..11 -> "Günaydın"
            in 12..17 -> "İyi günler"
            in 18..22 -> "İyi akşamlar"
            else -> "İyi geceler"
        }
    }

    private fun parseOrNull(isoDate: String): LocalDate? = runCatching {
        LocalDate.parse(isoDate, isoFormatter)
    }.getOrNull()
}
