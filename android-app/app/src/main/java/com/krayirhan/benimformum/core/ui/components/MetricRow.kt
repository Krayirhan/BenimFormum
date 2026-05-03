package com.krayirhan.benimformum.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.background
import androidx.compose.ui.unit.dp
import com.krayirhan.benimformum.core.ui.Spacing
import com.krayirhan.benimformum.ui.theme.appColors

@Composable
fun MetricRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    iconTint: Color? = null,
    iconBackground: Color? = null,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val appColors = MaterialTheme.appColors
    val resolvedTint = iconTint ?: appColors.privacy
    val resolvedBg = iconBackground ?: appColors.privacyContainer
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(resolvedBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = resolvedTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            modifier = Modifier
                .padding(start = Spacing.md)
                .weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor
        )
    }
}
