package com.krayirhan.benimformum.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krayirhan.benimformum.core.ui.Spacing
import com.krayirhan.benimformum.core.ui.components.AppCard
import com.krayirhan.benimformum.core.ui.components.AppCardStyle
import com.krayirhan.benimformum.core.ui.components.BarChart
import com.krayirhan.benimformum.core.ui.components.EmptyState
import com.krayirhan.benimformum.core.ui.components.LineChart
import com.krayirhan.benimformum.core.ui.components.MetricRow
import com.krayirhan.benimformum.core.ui.components.SectionTitle
import com.krayirhan.benimformum.core.util.AppDateFormatter
import com.krayirhan.benimformum.domain.usecase.WeightTrendPoint
import com.krayirhan.benimformum.ui.theme.AppShapes
import com.krayirhan.benimformum.ui.theme.BenimFormumTheme
import com.krayirhan.benimformum.ui.theme.appColors
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

private data class HistoryStat(
    val label: String,
    val value: String
)

@Composable
fun HistoryScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onNavigateToToday: () -> Unit = {},
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = Spacing.md, vertical = Spacing.md)
    ) {
        Text(
            text = "Son kayıtların, aralık içgörüleri ve hareketli ortalama buradan takip edilir.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val rangeDescription = when (state.selectedRange) {
            HistoryRange.LAST_7 -> "Geçmiş aralığı, son 7 gün seçili"
            HistoryRange.LAST_30 -> "Geçmiş aralığı, son 30 gün seçili"
        }
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.md)
                .semantics { contentDescription = rangeDescription }
        ) {
            val options = listOf(HistoryRange.LAST_7 to "Son 7 gün", HistoryRange.LAST_30 to "Son 30 gün")
            options.forEachIndexed { index, (range, label) ->
                SegmentedButton(
                    selected = state.selectedRange == range,
                    onClick = { viewModel.onRangeSelected(range) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        activeBorderColor = MaterialTheme.colorScheme.primary,
                        inactiveContainerColor = MaterialTheme.colorScheme.surface,
                        inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Text(label)
                }
            }
        }

        if (state.isEmpty) {
            EmptyState(
                icon = Icons.Filled.CalendarMonth,
                title = "Henüz geçmiş kayıt yok",
                description = "Bugün sekmesinden kaydını ekledikçe burada zaman içinde nasıl gittiğini sakin biçimde göreceksin.",
                actionLabel = "Bugün'e geç",
                onAction = onNavigateToToday,
                tip = "Çoğu kullanıcı 3. günden sonra küçük örüntüler fark etmeye başlar.",
                iconContentDescription = "Boş takvim ikonu"
            )
            return
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            contentPadding = PaddingValues(bottom = Spacing.xl)
        ) {
            item(key = "history-insight") { HistoryInsightCard(state.insight) }
            if (state.waterStats.recordedDays > 0) {
                item(key = "water-trend") { WaterTrendCard(state) }
            }
            if (state.weightStats.recordedDays > 0) {
                item(key = "weight-trend") { WeightTrendCard(state) }
            }
            stickyHeader(key = "history-daily-header") {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation = 0.dp
                ) {
                    Text(
                        text = "Günlük kayıtlar",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.sm, vertical = Spacing.sm),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            items(state.entries, key = { it.date }) { entry ->
                EntryCard(entry)
            }
        }
    }
}

@Composable
private fun HistoryInsightCard(insight: HistoryInsightUi) {
    AppCard(style = AppCardStyle.Insight) {
        SectionTitle(
            title = insight.title,
            subtitle = insight.description
        )
    }
}

@Composable
private fun WaterTrendCard(state: HistoryUiState) {
    val appColors = MaterialTheme.appColors
    val formatter = remember { NumberFormat.getIntegerInstance(Locale.forLanguageTag("tr-TR")) }
    val chartEntries = state.entries.reversed()
    val waterValues = chartEntries.map { it.waterMl.toFloat() }
    val stats = state.waterStats

    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                SectionTitle(
                    title = "Günlük su",
                    subtitle = "Kesik çizgi kişisel ${formatter.format(stats.goalMl)} ml hedefini gösterir."
                )
            }
            Text(
                text = "${stats.targetReachedDays} gün",
                style = MaterialTheme.typography.titleMedium,
                color = appColors.water
            )
        }
        Spacer(modifier = Modifier.height(Spacing.sm))
        StatGrid(
            stats = listOf(
                HistoryStat("Ortalama", "${formatter.format(stats.averageMl)} ml"),
                HistoryStat("En düşük", "${formatter.format(stats.minMl)} ml"),
                HistoryStat("En yüksek", "${formatter.format(stats.maxMl)} ml"),
                HistoryStat("Hedef", "${stats.targetReachedDays} / ${stats.recordedDays}")
            )
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        BarChart(
            values = waterValues,
            targetValue = stats.goalMl.toFloat(),
            barColor = appColors.water,
            summaryLabel = "Su kaydı olan ${stats.recordedDays} günün ortalaması ${formatter.format(stats.averageMl)} ml.",
            startLabel = chartEntries.firstOrNull()?.date?.let(AppDateFormatter::shortDate),
            endLabel = chartEntries.lastOrNull()?.date?.let(AppDateFormatter::shortDate),
            targetLabel = "Hedef ${formatter.format(stats.goalMl)} ml",
            contentDescription = "Su bar grafiği. Ortalama ${stats.averageMl} ml, hedefe ulaşılan gün sayısı ${stats.targetReachedDays}."
        )
    }
}

@Composable
private fun WeightTrendCard(state: HistoryUiState) {
    val appColors = MaterialTheme.appColors
    val stats = state.weightStats
    val deltaText = stats.deltaKg?.let(::formatDeltaKg) ?: "${stats.recordedDays} kayıt"
    val deltaColor = stats.deltaKg?.let { delta ->
        when {
            delta > 0.05 -> MaterialTheme.colorScheme.tertiary
            delta < -0.05 -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    } ?: MaterialTheme.colorScheme.onSurfaceVariant

    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                SectionTitle(
                    title = "Kilo trendi",
                    subtitle = "3 gün hareketli ortalama, yeterli veri varsa çizilir."
                )
            }
            Text(
                text = deltaText,
                style = MaterialTheme.typography.titleMedium,
                color = deltaColor
            )
        }
        Spacer(modifier = Modifier.height(Spacing.sm))
        StatGrid(
            stats = listOf(
                HistoryStat("Ortalama", formatKg(stats.averageKg)),
                HistoryStat("En düşük", formatKg(stats.minKg)),
                HistoryStat("En yüksek", formatKg(stats.maxKg)),
                HistoryStat("Kayıt", stats.recordedDays.toString())
            )
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        if (state.weightTrend.isNotEmpty()) {
            LineChart(
                values = state.weightTrend.map { it.movingAverage.toFloat() },
                lineColor = appColors.weight,
                fillTopColor = appColors.weight.copy(alpha = 0.18f),
                summaryLabel = "Hareketli ortalama ${formatKg(state.weightTrend.last().movingAverage)} ile bitiyor.",
                startLabel = AppDateFormatter.shortDate(state.weightTrend.first().date),
                endLabel = AppDateFormatter.shortDate(state.weightTrend.last().date),
                contentDescription = "Kilo trend çizgi grafiği. ${state.weightTrend.size} hareketli ortalama noktası var."
            )
        } else {
            Text(
                text = "Hareketli ortalama için en az 3 kilo kaydı gerekir. Mevcut kayıtlar aşağıdaki günlük kartlarda korunur.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatGrid(stats: List<HistoryStat>) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        stats.chunked(2).forEach { rowStats ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                rowStats.forEach { stat ->
                    StatPill(
                        label = stat.label,
                        value = stat.value,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowStats.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(AppShapes.card)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            .padding(horizontal = Spacing.sm, vertical = Spacing.sm)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                modifier = Modifier.padding(top = Spacing.xs),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun EntryCard(entry: HistoryEntryUi) {
    val appColors = MaterialTheme.appColors
    AppCard {
        Text(
            text = AppDateFormatter.friendlyDate(entry.date),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        MetricRow(
            icon = Icons.Filled.MonitorWeight,
            label = "Kilo",
            value = if (entry.weightText == "-") "—" else "${entry.weightText} kg",
            iconTint = appColors.onWeightContainer,
            iconBackground = appColors.weightContainer
        )
        MetricRow(
            icon = Icons.Filled.Bedtime,
            label = "Uyku",
            value = entry.sleepText,
            iconTint = appColors.onSleepContainer,
            iconBackground = appColors.sleepContainer
        )
        MetricRow(
            icon = Icons.Filled.Bolt,
            label = "Enerji",
            value = entry.energyText,
            iconTint = appColors.onEnergyContainer,
            iconBackground = appColors.energyContainer
        )
        MetricRow(
            icon = Icons.Filled.Mood,
            label = "Ruh hâli",
            value = entry.moodText,
            iconTint = appColors.onMoodContainer,
            iconBackground = appColors.moodContainer
        )
        MetricRow(
            icon = Icons.Filled.Cookie,
            label = "Gece",
            value = entry.nightSnackText,
            iconTint = appColors.onNightSnackContainer,
            iconBackground = appColors.nightSnackContainer
        )
        MetricRow(
            icon = Icons.Filled.WaterDrop,
            label = "Su",
            value = entry.waterText,
            iconTint = appColors.onWaterContainer,
            iconBackground = appColors.waterContainer
        )
    }
}

private fun formatKg(value: Double?): String {
    return value?.let { "${String.format(Locale.US, "%.1f", it)} kg" } ?: "—"
}

private fun formatDeltaKg(value: Double): String {
    return when {
        abs(value) < 0.05 -> "Sabit"
        value > 0 -> "+${String.format(Locale.US, "%.1f", value)} kg"
        else -> "${String.format(Locale.US, "%.1f", value)} kg"
    }
}

@Preview(
    name = "Portfolio - History",
    showBackground = true,
    widthDp = 360,
    heightDp = 760
)
@Composable
private fun HistoryPortfolioPreview() {
    val state = HistoryUiState(
        selectedRange = HistoryRange.LAST_7,
        entries = listOf(
            HistoryEntryUi(
                date = "2026-05-03",
                weightText = "72.4",
                sleepText = "4 / 5",
                energyText = "7 / 10",
                moodText = "4 / 5",
                nightSnackText = "Hayır",
                waterText = "1750 ml",
                waterMl = 1750
            ),
            HistoryEntryUi(
                date = "2026-05-02",
                weightText = "72.7",
                sleepText = "3 / 5",
                energyText = "6 / 10",
                moodText = "3 / 5",
                nightSnackText = "Evet",
                waterText = "2100 ml",
                waterMl = 2100
            ),
            HistoryEntryUi(
                date = "2026-05-01",
                weightText = "72.8",
                sleepText = "4 / 5",
                energyText = "8 / 10",
                moodText = "4 / 5",
                nightSnackText = "Hayır",
                waterText = "2000 ml",
                waterMl = 2000
            )
        ),
        weightTrend = listOf(
            WeightTrendPoint("2026-05-01", 72.8, 72.8),
            WeightTrendPoint("2026-05-02", 72.7, 72.7),
            WeightTrendPoint("2026-05-03", 72.4, 72.63)
        ),
        waterStats = WaterHistoryStats(
            recordedDays = 3,
            averageMl = 1950,
            minMl = 1750,
            maxMl = 2100,
            targetReachedDays = 2,
            goalMl = 2000
        ),
        weightStats = WeightHistoryStats(
            recordedDays = 3,
            averageKg = 72.63,
            minKg = 72.4,
            maxKg = 72.8,
            deltaKg = -0.4
        ),
        insight = HistoryInsightUi(
            title = "Hedefe ulaşılan günler var",
            description = "Son 7 gün içinde su hedefin 2 gün karşılandı. Diğer veriler günlük ayrıntılarda listelenir."
        ),
        isEmpty = false
    )

    BenimFormumTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            HistoryInsightCard(state.insight)
            WaterTrendCard(state)
            WeightTrendCard(state)
        }
    }
}
