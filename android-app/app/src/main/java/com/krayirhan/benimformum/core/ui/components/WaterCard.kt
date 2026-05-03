package com.krayirhan.benimformum.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.krayirhan.benimformum.core.ui.Spacing
import com.krayirhan.benimformum.ui.theme.NumericTitle
import com.krayirhan.benimformum.ui.theme.appColors
import java.text.NumberFormat
import java.util.Locale

@Composable
fun WaterCard(
    totalMl: Int,
    isAdding: Boolean,
    onAdd250: () -> Unit,
    modifier: Modifier = Modifier,
    goalMl: Int = 2000
) {
    val formatter = remember { NumberFormat.getIntegerInstance(Locale.forLanguageTag("tr-TR")) }
    val appColors = MaterialTheme.appColors
    val safeGoalMl = goalMl.coerceAtLeast(1)
    val ratio = (totalMl.toFloat() / safeGoalMl).coerceIn(0f, 1f)
    val animatedRatio by animateFloatAsState(
        targetValue = ratio,
        animationSpec = tween(durationMillis = 500),
        label = "waterRatio"
    )
    val goalText = "Hedef: ${formatter.format(safeGoalMl)} ml"

    AppCard(modifier = modifier, style = AppCardStyle.Insight) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(appColors.waterContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.WaterDrop,
                    contentDescription = null,
                    tint = appColors.onWaterContainer,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier
                .padding(start = Spacing.md)
                .weight(1f)) {
                Text(
                    text = "Su takibi",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${formatter.format(totalMl)} ml",
                    style = NumericTitle,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            PrimaryActionButton(
                text = "+250 ml",
                onClick = onAdd250,
                loading = isAdding,
                leadingIcon = Icons.Filled.Add
            )
        }
        LinearProgressIndicator(
            progress = { animatedRatio },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.md),
            color = appColors.water,
            trackColor = appColors.heroScoreTrack
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = goalText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${(animatedRatio * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
