package com.krayirhan.benimformum.feature.report

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krayirhan.benimformum.core.ui.Spacing
import com.krayirhan.benimformum.core.ui.components.AppCard
import com.krayirhan.benimformum.core.ui.components.AppCardStyle
import com.krayirhan.benimformum.core.ui.components.EmptyState
import com.krayirhan.benimformum.domain.model.WeeklySummaryItem
import com.krayirhan.benimformum.domain.model.WeeklySummaryTone
import com.krayirhan.benimformum.ui.theme.AppShapes
import com.krayirhan.benimformum.ui.theme.BenimFormumTheme
import com.krayirhan.benimformum.ui.theme.NumericDisplayMedium
import com.krayirhan.benimformum.ui.theme.NumericTitle
import com.krayirhan.benimformum.ui.theme.appColors

private data class SummaryAccent(
    val icon: ImageVector,
    val tint: Color,
    val background: Color,
    val metricColor: Color
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun WeeklySummaryScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onNavigateToToday: () -> Unit = {},
    viewModel: WeeklySummaryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        isRefreshing = state.isRefreshing,
        onRefresh = viewModel::refresh
    ) {
        Column {
            Text(
                text = "Son 7 günden kural tabanlı, sakin bir özet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            when {
                state.isLoading -> {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    WeeklySummaryLoadingSkeleton()
                }

                !state.hasData -> {
                    EmptyState(
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        title = "Bu hafta için özet yok",
                        description = "İlk günlük kaydını eklediğinde burada haftalık ilerlemeni sakin ve kural tabanlı bir özette göreceksin.",
                        actionLabel = "İlk kaydımı ekle",
                        onAction = onNavigateToToday,
                        tip = "Trend görmek için en az 3-4 günlük kayıt yeterli.",
                        iconContentDescription = "Yükselen trend ikonu"
                    )
                }

                else -> {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        contentPadding = PaddingValues(bottom = Spacing.xl)
                    ) {
                        item(key = "weekly-hero") {
                            WeeklyHeroCard(state)
                        }
                        items(state.items, key = { it.title }) { item ->
                            WeeklySummaryItemCard(item = item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklySummaryLoadingSkeleton() {
    val track = MaterialTheme.colorScheme.surfaceContainerHighest
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "Haftalık özet yükleniyor"
            },
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        AppCard(style = AppCardStyle.Hero) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(track)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(22.dp)
                            .clip(AppShapes.card)
                            .background(track)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(16.dp)
                            .clip(AppShapes.card)
                            .background(track)
                    )
                }
            }
        }
        repeat(3) {
            AppCard(style = AppCardStyle.Insight) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(track)
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .height(16.dp)
                                .clip(AppShapes.card)
                                .background(track)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .height(32.dp)
                                .clip(AppShapes.card)
                                .background(track)
                        )
                    }
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }
        }
    }
}

@Composable
private fun WeeklyHeroCard(state: WeeklySummaryUiState) {
    val appColors = MaterialTheme.appColors
    AppCard(style = AppCardStyle.Hero) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Box(
                modifier = Modifier.size(88.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { state.recordedDays / 7f },
                    modifier = Modifier.size(88.dp),
                    color = appColors.progressAccent,
                    strokeWidth = 8.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.recordedDays.toString(),
                        style = NumericDisplayMedium,
                        color = appColors.progressAccent
                    )
                    Text(
                        text = "/ 7",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.headline,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = state.helperText,
                    modifier = Modifier.padding(top = Spacing.xs),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WeeklySummaryItemCard(item: WeeklySummaryItem) {
    val accent = item.tone.accent()
    AppCard(style = AppCardStyle.Insight) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics(mergeDescendants = true) {
                    contentDescription = weeklySummaryItemAccessibilityLabel(item)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accent.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = accent.icon,
                    contentDescription = null,
                    tint = accent.tint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = Spacing.md)
                    .weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.description,
                    modifier = Modifier.padding(top = Spacing.xs),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item.metric?.let { metric ->
                Text(
                    text = metric,
                    modifier = Modifier.padding(start = Spacing.sm),
                    style = NumericTitle,
                    color = accent.metricColor
                )
            }
        }
    }
}

private fun weeklySummaryItemAccessibilityLabel(item: WeeklySummaryItem): String =
    buildString {
        append(item.title)
        append(". ")
        append(item.description)
        item.metric?.let { metric ->
            append(' ')
            append(metric)
        }
    }

@Composable
private fun WeeklySummaryTone.accent(): SummaryAccent {
    val appColors = MaterialTheme.appColors
    return when (this) {
        WeeklySummaryTone.WATER -> SummaryAccent(
            icon = Icons.Filled.WaterDrop,
            tint = appColors.onWaterContainer,
            background = appColors.waterContainer,
            metricColor = appColors.water
        )

        WeeklySummaryTone.ENERGY -> SummaryAccent(
            icon = Icons.Filled.Bolt,
            tint = appColors.onEnergyContainer,
            background = appColors.energyContainer,
            metricColor = appColors.energy
        )

        WeeklySummaryTone.MOOD -> SummaryAccent(
            icon = Icons.Filled.Mood,
            tint = appColors.onMoodContainer,
            background = appColors.moodContainer,
            metricColor = appColors.mood
        )

        WeeklySummaryTone.SLEEP -> SummaryAccent(
            icon = Icons.Filled.Bedtime,
            tint = appColors.onSleepContainer,
            background = appColors.sleepContainer,
            metricColor = appColors.sleep
        )

        WeeklySummaryTone.WEIGHT -> SummaryAccent(
            icon = Icons.Filled.MonitorWeight,
            tint = appColors.onWeightContainer,
            background = appColors.weightContainer,
            metricColor = appColors.weight
        )

        WeeklySummaryTone.FOOD -> SummaryAccent(
            icon = Icons.Filled.Cookie,
            tint = appColors.onNightSnackContainer,
            background = appColors.nightSnackContainer,
            metricColor = appColors.nightSnack
        )

        WeeklySummaryTone.NEUTRAL -> SummaryAccent(
            icon = Icons.Filled.CalendarMonth,
            tint = appColors.onPrivacyContainer,
            background = appColors.privacyContainer,
            metricColor = appColors.privacy
        )
    }
}

@Preview(
    name = "Portfolio - Weekly Summary",
    showBackground = true,
    widthDp = 360,
    heightDp = 760
)
@Composable
private fun WeeklySummaryPortfolioPreview() {
    val state = WeeklySummaryUiState(
        isLoading = false,
        hasData = true,
        headline = "Haftanın ritmi görünür hale geldi",
        helperText = "2000 ml su hedefin 4 gün karşılandı. Diğer metrikler yargısız gözlem olarak listelenir.",
        recordedDays = 5,
        items = listOf(
            WeeklySummaryItem(
                title = "Kayıt kapsamı",
                description = "Form veya su eklediğin günler haftalık kapsam olarak sayıldı.",
                metric = "5 / 7",
                tone = WeeklySummaryTone.NEUTRAL
            ),
            WeeklySummaryItem(
                title = "Su hedefi",
                description = "Kişisel su hedefin 4 gün karşılandı.",
                metric = "2050 ml",
                tone = WeeklySummaryTone.WATER
            ),
            WeeklySummaryItem(
                title = "Enerji",
                description = "Enerji kayıtların orta bantta, büyük bir uç göstermeden ilerlemiş.",
                metric = "6.8 / 10",
                tone = WeeklySummaryTone.ENERGY
            )
        )
    )

    BenimFormumTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            WeeklyHeroCard(state)
            state.items.forEach { WeeklySummaryItemCard(item = it) }
        }
    }
}
