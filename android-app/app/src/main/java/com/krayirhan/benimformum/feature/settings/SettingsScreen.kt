package com.krayirhan.benimformum.feature.settings

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.krayirhan.benimformum.R
import com.krayirhan.benimformum.core.ui.Spacing
import com.krayirhan.benimformum.core.ui.components.AppCard
import com.krayirhan.benimformum.core.ui.components.AppCardStyle
import com.krayirhan.benimformum.core.ui.components.AppSnackbarHost
import com.krayirhan.benimformum.core.ui.components.ObserveMessage
import com.krayirhan.benimformum.core.ui.components.PrimaryActionButton
import com.krayirhan.benimformum.core.ui.components.SecondaryActionButton
import com.krayirhan.benimformum.core.ui.components.SectionTitle
import com.krayirhan.benimformum.core.ui.components.rememberSnackbarHostState
import com.krayirhan.benimformum.domain.model.ThemePreference
import com.krayirhan.benimformum.domain.model.TrackedMetric
import com.krayirhan.benimformum.ui.theme.BenimFormumTheme
import com.krayirhan.benimformum.ui.theme.FormSpacing
import com.krayirhan.benimformum.ui.theme.appColors
import com.krayirhan.benimformum.ui.theme.brandFilterChipColors
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class MetricPreferenceOption(
    val metric: TrackedMetric,
    val label: String,
    val icon: ImageVector
)

@Composable
fun SettingsScreen(
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val versionName = remember(context) {
        runCatching {
            val pm = context.packageManager
            val pn = context.packageName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(pn, PackageManager.PackageInfoFlags.of(0)).versionName
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(pn, 0).versionName
            }
        }.getOrNull().orEmpty()
    }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = rememberSnackbarHostState()
    val appColors = MaterialTheme.appColors
    var pendingExportForWrite by remember { mutableStateOf<PreparedExport?>(null) }

    fun writeExportToUri(uri: Uri?, export: PreparedExport?) {
        if (uri == null || export == null) {
            viewModel.onExportCancelled()
            return
        }

        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val outputStream = context.contentResolver.openOutputStream(uri)
                        ?: error("Dosya açılamadı.")
                    outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
                        writer.write(export.content)
                    }
                }
            }
            viewModel.onExportWriteResult(export.fileName, result)
        }
    }

    val csvExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        val export = pendingExportForWrite
        pendingExportForWrite = null
        writeExportToUri(uri, export)
    }

    val jsonExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val export = pendingExportForWrite
        pendingExportForWrite = null
        writeExportToUri(uri, export)
    }

    ObserveMessage(
        message = state.message,
        snackbarHostState = snackbarHostState,
        onConsumed = viewModel::clearMessage
    )

    LaunchedEffect(state.pendingExport?.id) {
        val export = state.pendingExport ?: return@LaunchedEffect
        pendingExportForWrite = export
        viewModel.onExportLaunchConsumed()
        when (export.kind) {
            ExportKind.ALL_DATA_JSON -> jsonExportLauncher.launch(export.fileName)
            ExportKind.DAILY_FORMS_CSV,
            ExportKind.WATER_LOGS_CSV -> csvExportLauncher.launch(export.fileName)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.md, vertical = Spacing.md)
        ) {
            Text(
                text = stringResource(R.string.settings_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(Spacing.md))

            SectionTitle(title = stringResource(R.string.settings_section_preferences))
            Spacer(modifier = Modifier.height(Spacing.sm))

            WaterGoalSettingsCard(
                waterGoalMl = state.preferences.waterGoalMl,
                onWaterGoalChanged = viewModel::onWaterGoalChanged
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            ThemeSettingsCard(
                themePreference = state.preferences.themePreference,
                dynamicColorEnabled = state.preferences.dynamicColor,
                onThemePreferenceChanged = viewModel::onThemePreferenceChanged,
                onDynamicColorChanged = viewModel::onDynamicColorChanged
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            TrackingSettingsCard(
                selectedMetrics = state.preferences.trackedMetrics,
                onMetricToggled = viewModel::onTrackedMetricToggled
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(Spacing.md))

            SectionTitle(title = stringResource(R.string.settings_section_data_privacy))
            Spacer(modifier = Modifier.height(Spacing.sm))

            SettingsItemCard(
                icon = Icons.Filled.Folder,
                iconTint = appColors.onPrivacyContainer,
                iconBackground = appColors.privacyContainer,
                title = stringResource(R.string.settings_data_title),
                description = stringResource(R.string.settings_data_desc)
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            SettingsItemCard(
                icon = Icons.Filled.Lock,
                iconTint = appColors.onPrivacyContainer,
                iconBackground = appColors.privacyContainer,
                title = stringResource(R.string.settings_privacy_title),
                description = stringResource(R.string.settings_privacy_desc)
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(Spacing.md))

            SectionTitle(title = stringResource(R.string.settings_section_export))
            Spacer(modifier = Modifier.height(Spacing.sm))

            AppCard(style = AppCardStyle.Insight) {
                SectionTitle(
                    title = stringResource(R.string.settings_export_card_title),
                    subtitle = stringResource(R.string.settings_export_card_subtitle)
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                PrimaryActionButton(
                    text = stringResource(R.string.settings_export_json),
                    onClick = viewModel::prepareAllDataJsonExport,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.exportingKind == null || state.exportingKind == ExportKind.ALL_DATA_JSON,
                    loading = state.exportingKind == ExportKind.ALL_DATA_JSON,
                    leadingIcon = Icons.Filled.Save
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                SecondaryActionButton(
                    text = stringResource(R.string.settings_export_csv_forms),
                    onClick = viewModel::prepareDailyFormsCsvExport,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.exportingKind == null || state.exportingKind == ExportKind.DAILY_FORMS_CSV,
                    loading = state.exportingKind == ExportKind.DAILY_FORMS_CSV,
                    leadingIcon = Icons.Filled.Save
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                SecondaryActionButton(
                    text = stringResource(R.string.settings_export_csv_water),
                    onClick = viewModel::prepareWaterLogsCsvExport,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.exportingKind == null || state.exportingKind == ExportKind.WATER_LOGS_CSV,
                    loading = state.exportingKind == ExportKind.WATER_LOGS_CSV,
                    leadingIcon = Icons.Filled.Save
                )
                Text(
                    text = stringResource(R.string.settings_export_footer),
                    modifier = Modifier.padding(top = Spacing.md),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(Spacing.md))

            SectionTitle(title = stringResource(R.string.settings_section_about))
            Spacer(modifier = Modifier.height(Spacing.sm))

            AboutCard(versionName = versionName)

            Spacer(modifier = Modifier.height(FormSpacing.scrollContentTail))
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
private fun AboutCard(versionName: String) {
    AppCard {
        Text(
            text = stringResource(R.string.settings_about_app_line, stringResource(R.string.app_name)),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (versionName.isNotBlank()) {
                stringResource(R.string.settings_about_version, versionName)
            } else {
                stringResource(R.string.settings_about_version_unknown)
            },
            modifier = Modifier.padding(top = Spacing.sm),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.settings_about_privacy_placeholder),
            modifier = Modifier.padding(top = Spacing.md),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WaterGoalSettingsCard(
    waterGoalMl: Int,
    onWaterGoalChanged: (Int) -> Unit
) {
    val formatter = remember { NumberFormat.getIntegerInstance(Locale.forLanguageTag("tr-TR")) }
    val appColors = MaterialTheme.appColors
    val presets = remember { listOf(1500, 2000, 2500, 3000) }

    AppCard(style = AppCardStyle.Insight) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBubble(
                icon = Icons.Filled.WaterDrop,
                iconTint = appColors.onWaterContainer,
                iconBackground = appColors.waterContainer
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = Spacing.md)
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.settings_water_card_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.settings_water_card_subtitle),
                    modifier = Modifier.padding(top = Spacing.xs),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            ValuePill(text = "${formatter.format(waterGoalMl)} ml")
        }

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
            modifier = Modifier.padding(top = Spacing.sm),
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
}

@Composable
private fun ThemeSettingsCard(
    themePreference: ThemePreference,
    dynamicColorEnabled: Boolean,
    onThemePreferenceChanged: (ThemePreference) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit
) {
    AppCard(style = AppCardStyle.Insight) {
        SectionTitle(
            title = stringResource(R.string.settings_theme_title),
            subtitle = stringResource(R.string.settings_theme_subtitle)
        )
        Row(
            modifier = Modifier.padding(top = Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            ThemeChip(
                selected = themePreference == ThemePreference.SYSTEM,
                label = stringResource(R.string.settings_theme_system),
                icon = Icons.Filled.SettingsSuggest,
                onClick = { onThemePreferenceChanged(ThemePreference.SYSTEM) },
                modifier = Modifier.weight(1f)
            )
            ThemeChip(
                selected = themePreference == ThemePreference.LIGHT,
                label = stringResource(R.string.settings_theme_light),
                icon = Icons.Filled.LightMode,
                onClick = { onThemePreferenceChanged(ThemePreference.LIGHT) },
                modifier = Modifier.weight(1f)
            )
            ThemeChip(
                selected = themePreference == ThemePreference.DARK,
                label = stringResource(R.string.settings_theme_dark),
                icon = Icons.Filled.DarkMode,
                onClick = { onThemePreferenceChanged(ThemePreference.DARK) },
                modifier = Modifier.weight(1f)
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            HorizontalDivider(
                modifier = Modifier.padding(top = Spacing.md),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.settings_dynamic_color_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.settings_dynamic_color_subtitle),
                        modifier = Modifier.padding(top = Spacing.xs),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = dynamicColorEnabled,
                    onCheckedChange = onDynamicColorChanged
                )
            }
        }
    }
}

@Composable
private fun TrackingSettingsCard(
    selectedMetrics: Set<TrackedMetric>,
    onMetricToggled: (TrackedMetric) -> Unit
) {
    val options = listOf(
        MetricPreferenceOption(TrackedMetric.WATER, stringResource(R.string.metric_water), Icons.Filled.WaterDrop),
        MetricPreferenceOption(TrackedMetric.WEIGHT, stringResource(R.string.metric_weight), Icons.Filled.MonitorWeight),
        MetricPreferenceOption(TrackedMetric.SLEEP, stringResource(R.string.metric_sleep), Icons.Filled.Bedtime),
        MetricPreferenceOption(TrackedMetric.ENERGY, stringResource(R.string.metric_energy), Icons.Filled.Bolt),
        MetricPreferenceOption(TrackedMetric.MOOD, stringResource(R.string.metric_mood), Icons.Filled.Mood),
        MetricPreferenceOption(TrackedMetric.NIGHT_SNACK, stringResource(R.string.metric_night_snack), Icons.Filled.Cookie)
    )

    AppCard(style = AppCardStyle.Insight) {
        SectionTitle(
            title = stringResource(R.string.settings_tracking_title),
            subtitle = stringResource(R.string.settings_tracking_subtitle)
        )
        Column(
            modifier = Modifier.padding(top = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            options.chunked(2).forEach { rowOptions ->
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    rowOptions.forEach { option ->
                        FilterChip(
                            selected = option.metric in selectedMetrics,
                            onClick = { onMetricToggled(option.metric) },
                            label = { Text(option.label) },
                            leadingIcon = {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = brandFilterChipColors()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeChip(
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
private fun SettingsItemCard(
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color,
    title: String,
    description: String
) {
    AppCard(style = AppCardStyle.Insight) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBubble(icon, iconTint, iconBackground)
            Column(
                modifier = Modifier
                    .padding(horizontal = Spacing.md)
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    modifier = Modifier.padding(top = Spacing.xs),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ValuePill(text: String) {
    val appColors = MaterialTheme.appColors
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(appColors.privacyContainer.copy(alpha = 0.72f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = appColors.onPrivacyContainer
        )
    }
}

@Composable
private fun IconBubble(
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(iconBackground),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
    }
}

private const val MIN_WATER_GOAL_ML = 1000
private const val MAX_WATER_GOAL_ML = 4000

@Preview(
    name = "Portfolio - Settings",
    showBackground = true,
    widthDp = 360,
    heightDp = 760
)
@Composable
private fun SettingsPortfolioPreview() {
    BenimFormumTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Text(
                text = stringResource(R.string.settings_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            WaterGoalSettingsCard(
                waterGoalMl = 2000,
                onWaterGoalChanged = {}
            )
            ThemeSettingsCard(
                themePreference = ThemePreference.SYSTEM,
                dynamicColorEnabled = false,
                onThemePreferenceChanged = {},
                onDynamicColorChanged = {}
            )
            TrackingSettingsCard(
                selectedMetrics = TrackedMetric.values().toSet(),
                onMetricToggled = {}
            )
        }
    }
}
