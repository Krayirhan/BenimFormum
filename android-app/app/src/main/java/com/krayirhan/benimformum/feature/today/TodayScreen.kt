package com.krayirhan.benimformum.feature.today

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krayirhan.benimformum.R
import com.krayirhan.benimformum.core.ui.Spacing
import com.krayirhan.benimformum.core.ui.components.AppCard
import com.krayirhan.benimformum.core.ui.components.AppCardStyle
import com.krayirhan.benimformum.core.ui.components.AppSnackbarHost
import com.krayirhan.benimformum.core.ui.components.MetricSlider
import com.krayirhan.benimformum.core.ui.components.ObserveMessage
import com.krayirhan.benimformum.core.ui.components.PrimaryActionButton
import com.krayirhan.benimformum.core.ui.components.SecondaryActionButton
import com.krayirhan.benimformum.core.ui.components.SectionTitle
import com.krayirhan.benimformum.core.ui.components.rememberSnackbarHostState
import com.krayirhan.benimformum.core.ui.components.scoreColor
import com.krayirhan.benimformum.core.util.AppDateFormatter
import com.krayirhan.benimformum.domain.model.AppPreferences
import com.krayirhan.benimformum.domain.model.TrackedMetric
import com.krayirhan.benimformum.ui.theme.AppShapes
import com.krayirhan.benimformum.ui.theme.NumericDisplayMedium
import com.krayirhan.benimformum.ui.theme.NumericTitle
import com.krayirhan.benimformum.ui.theme.BenimFormumTheme
import com.krayirhan.benimformum.ui.theme.appColors
import java.text.NumberFormat
import java.util.Locale

private enum class TodayMetric {
    WATER,
    WEIGHT,
    SLEEP,
    ENERGY,
    MOOD,
    NIGHT_SNACK
}

private val TodayMetric.trackedMetric: TrackedMetric
    get() = when (this) {
        TodayMetric.WATER -> TrackedMetric.WATER
        TodayMetric.WEIGHT -> TrackedMetric.WEIGHT
        TodayMetric.SLEEP -> TrackedMetric.SLEEP
        TodayMetric.ENERGY -> TrackedMetric.ENERGY
        TodayMetric.MOOD -> TrackedMetric.MOOD
        TodayMetric.NIGHT_SNACK -> TrackedMetric.NIGHT_SNACK
    }

@Composable
fun TodayScreen(
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: DailyFormViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = rememberSnackbarHostState()
    var selectedMetric by rememberSaveable { mutableStateOf<TodayMetric?>(null) }

    ObserveMessage(
        message = state.message,
        snackbarHostState = snackbarHostState,
        onConsumed = viewModel::clearMessage
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.md, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            HeaderRow(date = state.date)

            DashboardHeroCard(state = state)

            if (TrackedMetric.WATER in state.trackedMetrics) {
                QuickWaterCard(
                    totalMl = state.waterTotalMl,
                    goalMl = state.waterGoalMl,
                    isAdding = state.isAddingWater,
                    onAdd250 = viewModel::addWaterQuick250,
                    onAdd500 = viewModel::addWaterQuick500
                )
            }

            MetricGrid(
                state = state,
                selectedMetric = selectedMetric,
                onMetricSelected = { metric ->
                    selectedMetric = if (selectedMetric == metric) null else metric
                }
            )

            selectedMetric?.let { metric ->
                MetricEditorCard(
                    metric = metric,
                    state = state,
                    viewModel = viewModel,
                    onSaved = { selectedMetric = null }
                )
            }

            DailyNoteCard(state = state, viewModel = viewModel)

            Spacer(modifier = Modifier.height(Spacing.xl))
        }

        AppSnackbarHost(
            state = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(Spacing.md)
        )
    }
}

@Composable
private fun HeaderRow(date: String) {
    val greeting = remember { AppDateFormatter.greeting() }
    val friendly = remember(date) { AppDateFormatter.friendlyDate(date) }
    Column {
        Text(
            text = greeting,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (friendly.isBlank()) {
                stringResource(R.string.today_header_fallback)
            } else {
                friendly
            },
            modifier = Modifier.padding(top = Spacing.xs),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DashboardHeroCard(state: DailyFormUiState) {
    val score = state.formScore.coerceIn(0, 100)
    val progress = score / 100f
    val scoreTint = scoreColor(score)
    val completed = completedMetricCount(state)
    val total = totalMetricCount(state)
    val helper = when {
        completed == 0 -> stringResource(R.string.today_hero_helper_none)
        completed < total -> stringResource(R.string.today_hero_helper_partial)
        else -> stringResource(R.string.today_hero_helper_complete)
    }
    val scoreContentDescription = stringResource(R.string.today_hero_score_a11y, score)

    AppCard(style = AppCardStyle.Hero) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clearAndSetSemantics {
                        contentDescription = scoreContentDescription
                    },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(92.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 8.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(92.dp),
                    color = scoreTint,
                    strokeWidth = 8.dp,
                    trackColor = Color.Transparent
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = score.toString(),
                        style = NumericDisplayMedium,
                        color = scoreTint
                    )
                    Text(
                        text = stringResource(R.string.today_hero_score_suffix),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.today_hero_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = helper,
                    modifier = Modifier.padding(top = Spacing.xs),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                CompletionStrip(
                    completed = completed,
                    total = total,
                    modifier = Modifier.padding(top = Spacing.md)
                )
            }
        }
    }
}

@Composable
private fun CompletionStrip(
    completed: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val safeTotal = total.coerceAtLeast(1)
    val percent = ((completed.toFloat() / safeTotal) * 100).toInt()
    val stripDescription = stringResource(R.string.today_completion_a11y, completed, total, percent)
    val stripLabel = stringResource(R.string.today_completion_fields, completed, total)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = stripDescription
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stripLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        LinearProgressIndicator(
            progress = { completed.toFloat() / safeTotal },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.xs),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun QuickWaterCard(
    totalMl: Int,
    goalMl: Int,
    isAdding: Boolean,
    onAdd250: () -> Unit,
    onAdd500: () -> Unit
) {
    val formatter = remember { NumberFormat.getIntegerInstance(Locale.forLanguageTag("tr-TR")) }
    val safeGoalMl = goalMl.coerceAtLeast(1)
    val ratio = (totalMl.toFloat() / safeGoalMl).coerceIn(0f, 1f)
    val goalLine = stringResource(R.string.today_water_goal_line, formatter.format(safeGoalMl))

    val appColors = MaterialTheme.appColors

    AppCard(style = AppCardStyle.Insight) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            IconBubble(
                icon = Icons.Filled.WaterDrop,
                tint = appColors.onWaterContainer,
                background = appColors.waterContainer
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.today_water_tracking),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${formatter.format(totalMl)} ml",
                    style = NumericTitle,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = goalLine,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LinearProgressIndicator(
            progress = { ratio },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.md),
            color = appColors.water,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            PrimaryActionButton(
                text = stringResource(R.string.today_water_add_250),
                onClick = onAdd250,
                modifier = Modifier.weight(1f),
                loading = isAdding,
                leadingIcon = Icons.Filled.Add
            )
            SecondaryActionButton(
                text = stringResource(R.string.today_water_add_500),
                onClick = onAdd500,
                modifier = Modifier.weight(1f),
                enabled = !isAdding,
                leadingIcon = Icons.Filled.Add
            )
        }
    }
}

@Composable
private fun metricLabel(metric: TodayMetric): String = stringResource(
    when (metric) {
        TodayMetric.WATER -> R.string.metric_water
        TodayMetric.WEIGHT -> R.string.metric_weight
        TodayMetric.SLEEP -> R.string.metric_sleep
        TodayMetric.ENERGY -> R.string.metric_energy
        TodayMetric.MOOD -> R.string.metric_mood
        TodayMetric.NIGHT_SNACK -> R.string.metric_night_snack
    }
)

@Composable
private fun MetricGrid(
    state: DailyFormUiState,
    selectedMetric: TodayMetric?,
    onMetricSelected: (TodayMetric) -> Unit
) {
    val visibleMetrics = remember(state.trackedMetrics) {
        listOf(
            TodayMetric.WEIGHT,
            TodayMetric.SLEEP,
            TodayMetric.ENERGY,
            TodayMetric.MOOD,
            TodayMetric.WATER,
            TodayMetric.NIGHT_SNACK
        ).filter { it.trackedMetric in state.trackedMetrics }
    }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        visibleMetrics.chunked(2).forEach { rowMetrics ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                rowMetrics.forEach { metric ->
                    MetricTileFor(
                        metric = metric,
                        state = state,
                        selected = selectedMetric == metric,
                        onMetricSelected = onMetricSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowMetrics.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MetricTileFor(
    metric: TodayMetric,
    state: DailyFormUiState,
    selected: Boolean,
    onMetricSelected: (TodayMetric) -> Unit,
    modifier: Modifier = Modifier
) {
    val notEntered = stringResource(R.string.today_metric_not_selected)
    val yes = stringResource(R.string.today_yes)
    val no = stringResource(R.string.today_no)
    val kg = stringResource(R.string.today_weight_unit)
    val label = metricLabel(metric)
    when (metric) {
        TodayMetric.WEIGHT -> MetricTile(
            metric = metric,
            icon = Icons.Filled.MonitorWeight,
            label = label,
            value = weightValue(state, notEntered, kg),
            entered = state.weightInput.isNotBlank(),
            selected = selected,
            iconTint = MaterialTheme.appColors.onWeightContainer,
            iconBackground = MaterialTheme.appColors.weightContainer,
            onClick = onMetricSelected,
            modifier = modifier
        )

        TodayMetric.SLEEP -> MetricTile(
            metric = metric,
            icon = Icons.Filled.Bedtime,
            label = label,
            value = sleepValue(state, notEntered),
            entered = state.sleepQualityInput.isNotBlank(),
            selected = selected,
            iconTint = MaterialTheme.appColors.onSleepContainer,
            iconBackground = MaterialTheme.appColors.sleepContainer,
            onClick = onMetricSelected,
            modifier = modifier
        )

        TodayMetric.ENERGY -> MetricTile(
            metric = metric,
            icon = Icons.Filled.Bolt,
            label = label,
            value = energyValue(state, notEntered),
            entered = state.energyScoreInput.isNotBlank(),
            selected = selected,
            iconTint = MaterialTheme.appColors.onEnergyContainer,
            iconBackground = MaterialTheme.appColors.energyContainer,
            valueColor = state.energyScoreInput.toIntOrNull()?.let { scoreColor(energyBand(it)) }
                ?: MaterialTheme.colorScheme.onSurface,
            onClick = onMetricSelected,
            modifier = modifier
        )

        TodayMetric.MOOD -> MetricTile(
            metric = metric,
            icon = Icons.Filled.Mood,
            label = label,
            value = moodValue(state, notEntered),
            entered = state.moodScoreInput.isNotBlank(),
            selected = selected,
            iconTint = MaterialTheme.appColors.onMoodContainer,
            iconBackground = MaterialTheme.appColors.moodContainer,
            valueColor = state.moodScoreInput.toIntOrNull()?.let { scoreColor(moodBand(it)) }
                ?: MaterialTheme.colorScheme.onSurface,
            onClick = onMetricSelected,
            modifier = modifier
        )

        TodayMetric.WATER -> MetricTile(
            metric = metric,
            icon = Icons.Filled.WaterDrop,
            label = label,
            value = waterValue(state, notEntered),
            entered = state.waterTotalMl > 0,
            selected = selected,
            iconTint = MaterialTheme.appColors.onWaterContainer,
            iconBackground = MaterialTheme.appColors.waterContainer,
            onClick = onMetricSelected,
            modifier = modifier
        )

        TodayMetric.NIGHT_SNACK -> MetricTile(
            metric = metric,
            icon = Icons.Filled.Cookie,
            label = label,
            value = nightSnackValue(state, yes, no, notEntered),
            entered = state.nightSnackDone != null,
            selected = selected,
            iconTint = MaterialTheme.appColors.onNightSnackContainer,
            iconBackground = MaterialTheme.appColors.nightSnackContainer,
            onClick = onMetricSelected,
            modifier = modifier
        )
    }
}

@Composable
private fun MetricTile(
    metric: TodayMetric,
    icon: ImageVector,
    label: String,
    value: String,
    entered: Boolean,
    selected: Boolean,
    onClick: (TodayMetric) -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    iconBackground: Color = MaterialTheme.colorScheme.primaryContainer,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.18f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }
    val statusText = stringResource(
        if (entered) R.string.today_status_entered else R.string.today_status_missing
    )
    val a11yLabel = metricTileAccessibilityLabel(label, value, entered, selected)

    Surface(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .heightIn(min = 112.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = a11yLabel
            }
            .clickable(role = Role.Button) { onClick(metric) },
        shape = AppShapes.card,
        color = containerColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(0.5.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBubble(icon = icon, tint = iconTint, background = iconBackground)
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MetricEditorCard(
    metric: TodayMetric,
    state: DailyFormUiState,
    viewModel: DailyFormViewModel,
    onSaved: () -> Unit
) {
    AppCard(style = AppCardStyle.Insight) {
        SectionTitle(
            title = metric.editorTitleString(),
            subtitle = metric.editorSubtitleString()
        )

        when (metric) {
            TodayMetric.WATER -> WaterEditor(viewModel = viewModel, isAdding = state.isAddingWater)
            TodayMetric.WEIGHT -> WeightEditor(state = state, viewModel = viewModel)
            TodayMetric.SLEEP -> SleepEditor(state = state, viewModel = viewModel)
            TodayMetric.ENERGY -> EnergyEditor(state = state, viewModel = viewModel)
            TodayMetric.MOOD -> MoodEditor(state = state, viewModel = viewModel)
            TodayMetric.NIGHT_SNACK -> NightSnackEditor(state = state, viewModel = viewModel)
        }

        if (metric != TodayMetric.WATER) {
            PrimaryActionButton(
                text = stringResource(R.string.today_save_metric),
                onClick = {
                    viewModel.saveTodayForm()
                    onSaved()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.md),
                enabled = !state.isAddingWater,
                loading = state.isSaving,
                leadingIcon = if (state.isSaving) null else Icons.Filled.Save
            )
        }
    }
}

@Composable
private fun WaterEditor(
    viewModel: DailyFormViewModel,
    isAdding: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        PrimaryActionButton(
            text = stringResource(R.string.today_water_add_250),
            onClick = viewModel::addWaterQuick250,
            modifier = Modifier.weight(1f),
            loading = isAdding,
            leadingIcon = Icons.Filled.Add
        )
        SecondaryActionButton(
            text = stringResource(R.string.today_water_add_500),
            onClick = viewModel::addWaterQuick500,
            modifier = Modifier.weight(1f),
            enabled = !isAdding,
            leadingIcon = Icons.Filled.Add
        )
    }
}

@Composable
private fun WeightEditor(
    state: DailyFormUiState,
    viewModel: DailyFormViewModel
) {
    OutlinedTextField(
        value = state.weightInput,
        onValueChange = viewModel::onWeightChanged,
        modifier = Modifier
            .padding(top = Spacing.md)
            .fillMaxWidth(),
        label = { Text(stringResource(R.string.today_weight_label)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        suffix = {
            Text(
                text = stringResource(R.string.today_weight_unit),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

@Composable
private fun SleepEditor(
    state: DailyFormUiState,
    viewModel: DailyFormViewModel
) {
    val resources = LocalContext.current.resources
    val skipLabel = stringResource(R.string.today_skip)
    val sleepValue = state.sleepQualityInput.toIntOrNull()?.coerceIn(0, 5) ?: 0
    MetricSlider(
        label = stringResource(R.string.today_sleep_quality_label),
        value = sleepValue,
        valueRange = 0..5,
        helper = stringResource(R.string.today_sleep_quality_helper),
        modifier = Modifier.padding(top = Spacing.md),
        valueLabel = { v ->
            if (v == 0) skipLabel else resources.getString(R.string.today_sleep_value_format, v)
        },
        onValueChange = { value ->
            if (value == 0) viewModel.onSleepQualityChanged("")
            else viewModel.onSleepQualityChanged(value.toString())
        }
    )
}

@Composable
private fun EnergyEditor(
    state: DailyFormUiState,
    viewModel: DailyFormViewModel
) {
    val resources = LocalContext.current.resources
    val energyValue = state.energyScoreInput.toIntOrNull()?.coerceIn(1, 10) ?: 5
    val energyEntered = state.energyScoreInput.isNotBlank()
    val energyUnset = stringResource(R.string.today_energy_value_unset)
    MetricSlider(
        label = stringResource(R.string.today_energy_label),
        value = energyValue,
        valueRange = 1..10,
        helper = stringResource(R.string.today_energy_helper),
        modifier = Modifier.padding(top = Spacing.md),
        valueLabel = { value ->
            if (energyEntered) resources.getString(R.string.today_energy_value_format, value) else energyUnset
        },
        onValueChange = { value -> viewModel.onEnergyChanged(value.toString()) }
    )
}

@Composable
private fun MoodEditor(
    state: DailyFormUiState,
    viewModel: DailyFormViewModel
) {
    val resources = LocalContext.current.resources
    val moodValue = state.moodScoreInput.toIntOrNull()?.coerceIn(1, 5) ?: 3
    val moodEntered = state.moodScoreInput.isNotBlank()
    val moodUnset = stringResource(R.string.today_metric_not_selected)
    MetricSlider(
        label = stringResource(R.string.today_mood_label),
        value = moodValue,
        valueRange = 1..5,
        helper = stringResource(R.string.today_mood_helper),
        modifier = Modifier.padding(top = Spacing.md),
        valueLabel = { value ->
            if (moodEntered) resources.getString(R.string.today_mood_value_format, value) else moodUnset
        },
        onValueChange = { value -> viewModel.onMoodChanged(value.toString()) }
    )
}

@Composable
private fun NightSnackEditor(
    state: DailyFormUiState,
    viewModel: DailyFormViewModel
) {
    val nightSnackQuestion = stringResource(R.string.today_night_snack_question)
    val nightSnackUnset = stringResource(R.string.today_night_snack_unset)
    Column(modifier = Modifier.padding(top = Spacing.md)) {
        Text(
            text = if (state.nightSnackDone != null) nightSnackQuestion else nightSnackUnset,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.padding(top = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            FilterChip(
                selected = state.nightSnackDone == false,
                onClick = { viewModel.onNightSnackChanged(false) },
                label = { Text(stringResource(R.string.today_no)) }
            )
            FilterChip(
                selected = state.nightSnackDone == true,
                onClick = { viewModel.onNightSnackChanged(true) },
                label = { Text(stringResource(R.string.today_yes)) }
            )
        }
    }
}

@Composable
private fun DailyNoteCard(
    state: DailyFormUiState,
    viewModel: DailyFormViewModel
) {
    AppCard(style = AppCardStyle.Standard) {
        SectionTitle(
            title = stringResource(R.string.today_note_title),
            subtitle = stringResource(R.string.today_note_subtitle)
        )
        OutlinedTextField(
            value = state.noteInput,
            onValueChange = viewModel::onNoteChanged,
            modifier = Modifier
                .padding(top = Spacing.md)
                .fillMaxWidth(),
            label = { Text(stringResource(R.string.today_note_field_label)) },
            minLines = 2
        )
        PrimaryActionButton(
            text = stringResource(R.string.today_save_day),
            onClick = viewModel::saveTodayForm,
            modifier = Modifier
                .padding(top = Spacing.md)
                .fillMaxWidth(),
            enabled = !state.isAddingWater,
            loading = state.isSaving,
            leadingIcon = if (state.isSaving) null else Icons.Filled.Save
        )
    }
}

/**
 * Küçük metrik ikonları; [MetricTile] üst düğümünde birleşik `contentDescription` kullanıldığında
 * ayrı ikon açıklaması gerekmez (Sprint 2 — S2-5).
 */
@Composable
private fun IconBubble(
    icon: ImageVector,
    tint: Color,
    background: Color
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun metricTileAccessibilityLabel(
    label: String,
    value: String,
    entered: Boolean,
    selected: Boolean
): String {
    val statusWord = stringResource(
        if (entered) R.string.today_status_entered else R.string.today_status_missing
    )
    val prefix = if (selected) {
        stringResource(R.string.today_metric_tile_a11y_selected_prefix)
    } else {
        ""
    }
    return stringResource(R.string.today_metric_tile_a11y, prefix, label, value, statusWord)
}

private fun completedMetricCount(state: DailyFormUiState): Int {
    return state.trackedMetrics.count { metric ->
        when (metric) {
            TrackedMetric.WATER -> state.waterTotalMl > 0
            TrackedMetric.WEIGHT -> state.weightInput.isNotBlank()
            TrackedMetric.SLEEP -> state.sleepQualityInput.isNotBlank()
            TrackedMetric.ENERGY -> state.energyScoreInput.isNotBlank()
            TrackedMetric.MOOD -> state.moodScoreInput.isNotBlank()
            TrackedMetric.NIGHT_SNACK -> state.nightSnackDone != null
        }
    }
}

private fun totalMetricCount(state: DailyFormUiState): Int {
    return state.trackedMetrics.size.coerceAtLeast(1)
}

private fun weightValue(state: DailyFormUiState, notEntered: String, kgSuffix: String): String {
    return state.weightInput.trim().ifBlank { null }?.let { "$it $kgSuffix" } ?: notEntered
}

private fun sleepValue(state: DailyFormUiState, notEntered: String): String {
    val value = state.sleepQualityInput.toIntOrNull()
    return if (value in 1..5) "$value / 5" else notEntered
}

private fun energyValue(state: DailyFormUiState, notEntered: String): String {
    val value = state.energyScoreInput.toIntOrNull()
    return if (value in 1..10) "$value / 10" else notEntered
}

private fun moodValue(state: DailyFormUiState, notEntered: String): String {
    val value = state.moodScoreInput.toIntOrNull()
    return if (value in 1..5) "$value / 5" else notEntered
}

private fun nightSnackValue(state: DailyFormUiState, yes: String, no: String, notEntered: String): String {
    return when (state.nightSnackDone) {
        true -> yes
        false -> no
        null -> notEntered
    }
}

private fun waterValue(state: DailyFormUiState, notEntered: String): String {
    return if (state.waterTotalMl > 0) "${state.waterTotalMl} ml" else notEntered
}

private fun energyBand(score: Int) = when {
    score <= 0 -> 0
    score <= 3 -> 30
    score <= 7 -> 60
    else -> 90
}

private fun moodBand(score: Int) = when {
    score <= 0 -> 0
    score <= 2 -> 30
    score == 3 -> 60
    else -> 90
}

@Composable
private fun TodayMetric.editorTitleString(): String = stringResource(
    when (this) {
        TodayMetric.WATER -> R.string.today_editor_water_title
        TodayMetric.WEIGHT -> R.string.today_editor_weight_title
        TodayMetric.SLEEP -> R.string.today_sleep_quality_label
        TodayMetric.ENERGY -> R.string.today_energy_label
        TodayMetric.MOOD -> R.string.today_mood_label
        TodayMetric.NIGHT_SNACK -> R.string.today_editor_night_snack_title
    }
)

@Composable
private fun TodayMetric.editorSubtitleString(): String = stringResource(
    when (this) {
        TodayMetric.WATER -> R.string.today_editor_water_subtitle
        TodayMetric.WEIGHT -> R.string.today_editor_weight_subtitle
        TodayMetric.SLEEP -> R.string.today_editor_sleep_subtitle
        TodayMetric.ENERGY -> R.string.today_editor_energy_subtitle
        TodayMetric.MOOD -> R.string.today_editor_mood_subtitle
        TodayMetric.NIGHT_SNACK -> R.string.today_editor_night_snack_subtitle
    }
)

@Preview(
    name = "Portfolio - Today",
    showBackground = true,
    widthDp = 360,
    heightDp = 760
)
@Composable
private fun TodayPortfolioPreview() {
    val state = DailyFormUiState(
        date = "2026-05-03",
        weightInput = "72.4",
        sleepQualityInput = "4",
        energyScoreInput = "7",
        moodScoreInput = "4",
        nightSnackDone = false,
        noteInput = "Kısa ve sakin bir gün.",
        waterTotalMl = 1750,
        waterGoalMl = AppPreferences.DEFAULT_WATER_GOAL_ML,
        trackedMetrics = AppPreferences.DEFAULT_TRACKED_METRICS,
        formScore = 78
    )

    BenimFormumTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            HeaderRow(date = state.date)
            DashboardHeroCard(state = state)
            QuickWaterCard(
                totalMl = state.waterTotalMl,
                goalMl = state.waterGoalMl,
                isAdding = false,
                onAdd250 = {},
                onAdd500 = {}
            )
            MetricGrid(
                state = state,
                selectedMetric = null,
                onMetricSelected = {}
            )
        }
    }
}
