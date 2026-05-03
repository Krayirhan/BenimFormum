package com.krayirhan.benimformum.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.krayirhan.benimformum.ui.theme.appColors
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BarChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    height: Dp = 120.dp,
    barColor: Color? = null,
    inactiveBarColor: Color? = null,
    gridColor: Color? = null,
    targetValue: Float? = null,
    summaryLabel: String? = null,
    startLabel: String? = null,
    endLabel: String? = null,
    targetLabel: String? = null,
    contentDescription: String? = null
) {
    val appColors = MaterialTheme.appColors
    val scheme = MaterialTheme.colorScheme
    val resolvedBar = barColor ?: appColors.progressAccent
    val resolvedInactive = inactiveBarColor ?: appColors.heroScoreTrack
    val resolvedGrid = gridColor ?: scheme.outlineVariant
    val semanticLabel = contentDescription
        ?: summaryLabel
        ?: "Bar grafik, ${values.size} veri noktası"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { this.contentDescription = semanticLabel }
    ) {
        summaryLabel?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            if (values.isEmpty()) return@Box
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
            ) {
                drawGrid(resolvedGrid)
                val maxValue = (listOfNotNull(targetValue) + values).max().coerceAtLeast(1f)
                val padTop = size.height * 0.10f
                val padBot = size.height * 0.05f
                val usableHeight = size.height - padTop - padBot
                val barCount = values.size
                val totalGapRatio = 0.35f
                val barWidth = size.width * (1f - totalGapRatio) / barCount
                val gap = size.width * totalGapRatio / (barCount + 1)

                values.forEachIndexed { index, value ->
                    val x = gap + index * (barWidth + gap)
                    val barHeight = (value / maxValue) * usableHeight
                    val y = padTop + (usableHeight - barHeight)
                    val color = if (value > 0f) resolvedBar else resolvedInactive
                    drawRoundedBar(
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight.coerceAtLeast(2.dp.toPx())),
                        color = color
                    )
                }

                if (targetValue != null && targetValue > 0f) {
                    val targetY = padTop + (1f - targetValue / maxValue) * usableHeight
                    drawLine(
                        color = resolvedBar.copy(alpha = 0.55f),
                        start = Offset(0f, targetY),
                        end = Offset(size.width, targetY),
                        strokeWidth = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                    )
                }
            }
        }
        if (targetLabel != null || startLabel != null || endLabel != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = startLabel.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = targetLabel.orEmpty().ifBlank { endLabel.orEmpty() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (targetLabel != null && endLabel != null) {
                    Text(
                        text = endLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawRoundedBar(topLeft: Offset, size: Size, color: Color) {
    drawRoundRect(
        color = color,
        topLeft = topLeft,
        size = size,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
    )
}

private fun DrawScope.drawGrid(gridColor: Color) {
    val rows = 3
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 8f), 0f)
    for (i in 0..rows) {
        val y = size.height * (i.toFloat() / rows)
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f,
            pathEffect = dashEffect
        )
    }
}
