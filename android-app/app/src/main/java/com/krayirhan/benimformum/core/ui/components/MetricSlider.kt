package com.krayirhan.benimformum.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.krayirhan.benimformum.core.ui.Spacing
import com.krayirhan.benimformum.core.ui.rememberAppHaptics
import com.krayirhan.benimformum.ui.theme.appColors

@Composable
fun MetricSlider(
    label: String,
    value: Int,
    valueRange: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    helper: String? = null,
    valueLabel: (Int) -> String = { v ->
        if (v <= 0 && valueRange.first == 0) "Atla" else "$v / ${valueRange.last}"
    }
) {
    val currentValueLabel = valueLabel(value)
    val appColors = MaterialTheme.appColors
    val accent = appColors.privacy
    val rangeFloat = valueRange.first.toFloat()..valueRange.last.toFloat()
    val steps = (valueRange.last - valueRange.first - 1).coerceAtLeast(0)
    val haptics = rememberAppHaptics()
    val a11yLabel = "$label, $currentValueLabel"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = a11yLabel }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(accent.copy(alpha = 0.18f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = currentValueLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = accent
                )
            }
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { newValue ->
                val rounded = newValue.toInt()
                if (rounded != value) haptics.tap()
                onValueChange(rounded)
            },
            valueRange = rangeFloat,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent,
                inactiveTrackColor = appColors.heroScoreTrack,
                activeTickColor = MaterialTheme.colorScheme.surface,
                inactiveTickColor = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier.padding(top = Spacing.xs)
        )
        if (helper != null) {
            Text(
                text = helper,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
