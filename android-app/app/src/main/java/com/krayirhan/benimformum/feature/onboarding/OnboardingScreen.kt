package com.krayirhan.benimformum.feature.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.krayirhan.benimformum.core.ui.Spacing
import com.krayirhan.benimformum.core.ui.components.AppCard
import com.krayirhan.benimformum.core.ui.components.AppCardStyle
import com.krayirhan.benimformum.core.ui.components.PrimaryActionButton
import com.krayirhan.benimformum.core.ui.components.SecondaryActionButton
import com.krayirhan.benimformum.domain.model.AppPreferences
import com.krayirhan.benimformum.domain.model.ThemePreference
import com.krayirhan.benimformum.domain.model.TrackedMetric
import com.krayirhan.benimformum.R
import com.krayirhan.benimformum.ui.theme.BenimFormumTheme
import com.krayirhan.benimformum.ui.theme.appColors
import com.krayirhan.benimformum.ui.theme.brandFilterChipColors
import java.text.NumberFormat
import java.util.Locale

private data class TrackingOption(
    val metric: TrackedMetric,
    val label: String,
    val icon: ImageVector
)

private data class ThemeOption(
    val preference: ThemePreference,
    val label: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(
    onCompleted: (AppPreferences) -> Unit,
    onExitFromFirstStepBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentStep by rememberSaveable { mutableIntStateOf(0) }

    BackHandler {
        if (currentStep > 0) {
            currentStep -= 1
        } else {
            onExitFromFirstStepBack()
        }
    }
    var waterGoalMl by rememberSaveable { mutableIntStateOf(AppPreferences.DEFAULT_WATER_GOAL_ML) }
    var themePreference by rememberSaveable { mutableStateOf(ThemePreference.SYSTEM) }
    var selectedMetricNames by rememberSaveable {
        mutableStateOf(AppPreferences.DEFAULT_TRACKED_METRICS.map { it.name }.toSet())
    }
    val selectedMetrics = remember(selectedMetricNames) {
        selectedMetricNames.mapNotNull { name ->
            TrackedMetric.values().firstOrNull { it.name == name }
        }.toSet()
    }

    fun selectedPreferences(): AppPreferences {
        return AppPreferences(
            waterGoalMl = waterGoalMl.coerceIn(MIN_WATER_GOAL_ML, MAX_WATER_GOAL_ML),
            themePreference = themePreference,
            trackedMetrics = selectedMetrics.ifEmpty { AppPreferences.DEFAULT_TRACKED_METRICS }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.md, vertical = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.onboarding_app_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.onboarding_intro_subtitle),
            modifier = Modifier.padding(top = Spacing.xs),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        PageDots(pageCount = STEP_COUNT, currentPage = currentStep)

        Spacer(modifier = Modifier.height(Spacing.md))

        AppCard(style = AppCardStyle.Hero) {
            when (currentStep) {
                0 -> TrackingStep(
                    selectedMetrics = selectedMetrics,
                    onMetricToggled = { metric ->
                        selectedMetricNames = toggleMetric(selectedMetricNames, metric)
                    }
                )

                1 -> WaterGoalStep(
                    waterGoalMl = waterGoalMl,
                    onWaterGoalChanged = { waterGoalMl = it.coerceIn(MIN_WATER_GOAL_ML, MAX_WATER_GOAL_ML) }
                )

                else -> ThemeStep(
                    themePreference = themePreference,
                    onThemePreferenceChanged = { themePreference = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        if (currentStep > 0) {
            val appColors = MaterialTheme.appColors
            TextButton(
                onClick = { currentStep -= 1 },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = appColors.privacy,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            ) {
                Text(text = stringResource(R.string.onboarding_back))
            }
        }

        PrimaryActionButton(
            text = if (currentStep == STEP_COUNT - 1) {
                stringResource(R.string.onboarding_start_today)
            } else {
                stringResource(R.string.onboarding_continue)
            },
            onClick = {
                if (currentStep == STEP_COUNT - 1) {
                    onCompleted(selectedPreferences())
                } else {
                    currentStep += 1
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        SecondaryActionButton(
            text = stringResource(R.string.onboarding_defaults),
            onClick = { onCompleted(AppPreferences()) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.sm)
        )
    }
}

@Composable
private fun TrackingStep(
    selectedMetrics: Set<TrackedMetric>,
    onMetricToggled: (TrackedMetric) -> Unit
) {
    val options = listOf(
        TrackingOption(TrackedMetric.WATER, stringResource(R.string.metric_water), Icons.Filled.WaterDrop),
        TrackingOption(TrackedMetric.WEIGHT, stringResource(R.string.metric_weight), Icons.Filled.MonitorWeight),
        TrackingOption(TrackedMetric.SLEEP, stringResource(R.string.metric_sleep), Icons.Filled.Bedtime),
        TrackingOption(TrackedMetric.ENERGY, stringResource(R.string.metric_energy), Icons.Filled.Bolt),
        TrackingOption(TrackedMetric.MOOD, stringResource(R.string.metric_mood), Icons.Filled.Mood),
        TrackingOption(TrackedMetric.NIGHT_SNACK, stringResource(R.string.metric_night_snack), Icons.Filled.Cookie)
    )

    StepHeader(
        icon = Icons.Filled.SettingsSuggest,
        title = stringResource(R.string.onboarding_tracking_title),
        description = stringResource(R.string.onboarding_tracking_desc)
    )

    Column(
        modifier = Modifier.padding(top = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        options.chunked(2).forEach { rowOptions ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                rowOptions.forEach { option ->
                    MetricFilterChip(
                        selected = option.metric in selectedMetrics,
                        label = option.label,
                        icon = option.icon,
                        onClick = { onMetricToggled(option.metric) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowOptions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun WaterGoalStep(
    waterGoalMl: Int,
    onWaterGoalChanged: (Int) -> Unit
) {
    val formatter = remember { NumberFormat.getIntegerInstance(Locale.forLanguageTag("tr-TR")) }
    val presets = remember { listOf(1500, 2000, 2500, 3000) }

    StepHeader(
        icon = Icons.Filled.WaterDrop,
        title = stringResource(R.string.onboarding_water_title),
        description = stringResource(R.string.onboarding_water_desc)
    )

    Text(
        text = "${formatter.format(waterGoalMl)} ml",
        modifier = Modifier.padding(top = Spacing.lg),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )

    Row(
        modifier = Modifier.padding(top = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        SecondaryActionButton(
            text = stringResource(R.string.onboarding_water_minus),
            onClick = { onWaterGoalChanged(waterGoalMl - 250) },
            modifier = Modifier.weight(1f),
            enabled = waterGoalMl > MIN_WATER_GOAL_ML
        )
        SecondaryActionButton(
            text = stringResource(R.string.onboarding_water_plus),
            onClick = { onWaterGoalChanged(waterGoalMl + 250) },
            modifier = Modifier.weight(1f),
            enabled = waterGoalMl < MAX_WATER_GOAL_ML
        )
    }

    Column(
        modifier = Modifier.padding(top = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        presets.chunked(2).forEach { rowPresets ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                rowPresets.forEach { preset ->
                    FilterChip(
                        selected = waterGoalMl == preset,
                        onClick = { onWaterGoalChanged(preset) },
                        label = { Text("${formatter.format(preset)} ml") },
                        modifier = Modifier.weight(1f),
                        colors = brandFilterChipColors()
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeStep(
    themePreference: ThemePreference,
    onThemePreferenceChanged: (ThemePreference) -> Unit
) {
    val appColors = MaterialTheme.appColors
    val options = listOf(
        ThemeOption(
            preference = ThemePreference.SYSTEM,
            label = stringResource(R.string.onboarding_theme_system),
            description = stringResource(R.string.onboarding_theme_system_desc),
            icon = Icons.Filled.SettingsSuggest
        ),
        ThemeOption(
            preference = ThemePreference.LIGHT,
            label = stringResource(R.string.onboarding_theme_light),
            description = stringResource(R.string.onboarding_theme_light_desc),
            icon = Icons.Filled.LightMode
        ),
        ThemeOption(
            preference = ThemePreference.DARK,
            label = stringResource(R.string.onboarding_theme_dark),
            description = stringResource(R.string.onboarding_theme_dark_desc),
            icon = Icons.Filled.DarkMode
        )
    )

    StepHeader(
        icon = Icons.Filled.Lock,
        title = stringResource(R.string.onboarding_theme_title),
        description = stringResource(R.string.onboarding_theme_desc)
    )

    Column(
        modifier = Modifier.padding(top = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        options.forEach { option ->
            Surface(
                onClick = { onThemePreferenceChanged(option.preference) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = if (themePreference == option.preference) {
                    appColors.privacyContainer.copy(alpha = 0.55f)
                } else {
                    MaterialTheme.colorScheme.surface
                },
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = null,
                        tint = if (themePreference == option.preference) {
                            appColors.privacy
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(22.dp)
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = Spacing.md)
                            .weight(1f)
                    ) {
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = option.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (themePreference == option.preference) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = appColors.privacy,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = appColors.onPrivacyContainer,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(appColors.privacyContainer)
                    .padding(Spacing.sm)
                    .size(18.dp)
            )
            Text(
                text = stringResource(R.string.onboarding_privacy_note),
                modifier = Modifier.padding(start = Spacing.sm),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StepHeader(
    icon: ImageVector,
    title: String,
    description: String
) {
    val appColors = MaterialTheme.appColors
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(appColors.privacyContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = appColors.onPrivacyContainer,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(
            text = title,
            modifier = Modifier.padding(top = Spacing.lg),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = description,
            modifier = Modifier.padding(top = Spacing.sm),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MetricFilterChip(
    selected: Boolean,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
        modifier = modifier,
        colors = brandFilterChipColors()
    )
}

@Composable
private fun PageDots(
    pageCount: Int,
    currentPage: Int
) {
    val appColors = MaterialTheme.appColors
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            val dotDescription = if (selected) {
                stringResource(R.string.onboarding_dot_a11y_selected, index + 1, pageCount)
            } else {
                stringResource(R.string.onboarding_dot_a11y_not_selected, index + 1, pageCount)
            }
            Box(
                modifier = Modifier
                    .semantics { contentDescription = dotDescription }
                    .width(if (selected) 24.dp else 8.dp)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) {
                            appColors.privacy
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
            )
        }
    }
}

private fun toggleMetric(
    selectedMetricNames: Set<String>,
    metric: TrackedMetric
): Set<String> {
    return if (metric.name in selectedMetricNames) {
        if (selectedMetricNames.size == 1) selectedMetricNames else selectedMetricNames - metric.name
    } else {
        selectedMetricNames + metric.name
    }
}

private const val STEP_COUNT = 3
private const val MIN_WATER_GOAL_ML = 1000
private const val MAX_WATER_GOAL_ML = 4000

@Preview(
    name = "Portfolio - Onboarding",
    showBackground = true,
    widthDp = 360,
    heightDp = 760
)
@Composable
private fun OnboardingPortfolioPreview() {
    BenimFormumTheme {
        OnboardingScreen(onCompleted = {})
    }
}
