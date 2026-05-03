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
import androidx.compose.ui.unit.dp
import com.krayirhan.benimformum.ui.theme.AppShapes
import com.krayirhan.benimformum.ui.theme.FormHeroCardBorder
import com.krayirhan.benimformum.ui.theme.FormHeroCardBorderDark
import com.krayirhan.benimformum.ui.theme.FormHeroCardFill
import com.krayirhan.benimformum.ui.theme.FormHeroCardFillDark
import com.krayirhan.benimformum.ui.theme.FormTokens
import com.krayirhan.benimformum.ui.theme.LocalAppUsesDarkTheme
import com.krayirhan.benimformum.ui.theme.appColors

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
    contentPadding: PaddingValues = PaddingValues(
        horizontal = FormTokens.cardInnerHorizontal,
        vertical = FormTokens.cardInnerVertical
    ),
    content: @Composable () -> Unit
) {
    val appColors = MaterialTheme.appColors
    val darkTheme = LocalAppUsesDarkTheme.current
    val container = when (style) {
        AppCardStyle.Hero -> if (darkTheme) FormHeroCardFillDark else FormHeroCardFill
        AppCardStyle.Metric -> MaterialTheme.colorScheme.surface
        AppCardStyle.Insight -> appColors.insightCardFill
        AppCardStyle.Standard -> if (elevated) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        }
    }
    val borderColor = when (style) {
        AppCardStyle.Hero -> if (darkTheme) FormHeroCardBorderDark else FormHeroCardBorder
        AppCardStyle.Metric -> MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)
        AppCardStyle.Insight -> appColors.insightCardBorder.copy(alpha = 0.38f)
        AppCardStyle.Standard -> MaterialTheme.colorScheme.outlineVariant
    }
    val elevation = when (style) {
        AppCardStyle.Hero -> CardDefaults.cardElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
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
        border = BorderStroke(FormTokens.cardBorderWidth, borderColor)
    ) {
        Column(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
