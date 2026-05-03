package com.krayirhan.benimformum.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.krayirhan.benimformum.core.ui.Spacing
import com.krayirhan.benimformum.ui.theme.AppDesignTokens
import com.krayirhan.benimformum.ui.theme.AppShapes

enum class AppCardStyle {
    Standard,
    Hero,
    Metric,
    Insight
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    elevated: Boolean = false,
    style: AppCardStyle = AppCardStyle.Standard,
    contentPadding: PaddingValues = PaddingValues(Spacing.md),
    content: @Composable () -> Unit
) {
    val container = when (style) {
        AppCardStyle.Hero -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = AppDesignTokens.cardHeroFillAlpha)
        AppCardStyle.Metric -> MaterialTheme.colorScheme.surfaceContainerLow
        AppCardStyle.Insight -> MaterialTheme.colorScheme.surfaceContainerHigh.copy(
            alpha = AppDesignTokens.cardInsightFillAlpha
        )
        AppCardStyle.Standard -> if (elevated) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        }
    }
    val borderColor = when (style) {
        AppCardStyle.Hero -> MaterialTheme.colorScheme.primary.copy(alpha = AppDesignTokens.cardHeroBorderAlpha)
        AppCardStyle.Metric -> MaterialTheme.colorScheme.outlineVariant
        AppCardStyle.Insight -> MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
        AppCardStyle.Standard -> MaterialTheme.colorScheme.outlineVariant
    }
    val elevation = when (style) {
        AppCardStyle.Hero -> CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 2.dp)
        else -> CardDefaults.cardElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.card,
        colors = CardDefaults.cardColors(
            containerColor = container,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = elevation,
        border = BorderStroke(0.5.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
