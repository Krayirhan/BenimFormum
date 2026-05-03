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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LineChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    height: Dp = 140.dp,
    lineColor: Color? = null,
    fillTopColor: Color? = null,
    fillBottomColor: Color? = null,
    gridColor: Color? = null,
    summaryLabel: String? = null,
    startLabel: String? = null,
    endLabel: String? = null,
    contentDescription: String? = null
) {
    val appColors = MaterialTheme.appColors
    val scheme = MaterialTheme.colorScheme
    val resolvedLine = lineColor ?: appColors.progressAccent
    val resolvedFillTop = fillTopColor ?: resolvedLine.copy(alpha = 0.20f)
    val resolvedFillBottom = fillBottomColor ?: resolvedLine.copy(alpha = 0f)
    val resolvedGrid = gridColor ?: scheme.outlineVariant
    val points = values.filter { it.isFinite() }
    val semanticLabel = contentDescription
        ?: summaryLabel
        ?: "Çizgi grafik, ${points.size} veri noktası"

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
            if (points.size < 2) {
                return@Box
            }
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
            ) {
                drawGrid(resolvedGrid)
                val maxV = points.max()
                val minV = points.min()
                val range = (maxV - minV).coerceAtLeast(0.0001f)
                val padTop = size.height * 0.10f
                val padBot = size.height * 0.10f
                val usableHeight = size.height - padTop - padBot
                val stepX = if (points.size <= 1) size.width else size.width / (points.size - 1f)

                val mapped = points.mapIndexed { i, v ->
                    val x = i * stepX
                    val y = padTop + (1f - (v - minV) / range) * usableHeight
                    Offset(x, y)
                }

                val path = Path().apply {
                    moveTo(mapped.first().x, mapped.first().y)
                    for (i in 1 until mapped.size) {
                        val prev = mapped[i - 1]
                        val curr = mapped[i]
                        val midX = (prev.x + curr.x) / 2f
                        cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                    }
                }

                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(mapped.last().x, size.height)
                    lineTo(mapped.first().x, size.height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        listOf(resolvedFillTop, resolvedFillBottom),
                        startY = 0f,
                        endY = size.height
                    )
                )

                drawPath(
                    path = path,
                    color = resolvedLine,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                mapped.forEach { p ->
                    drawCircle(
                        color = resolvedLine,
                        radius = 3.dp.toPx(),
                        center = p
                    )
                }
            }
        }
        if (startLabel != null || endLabel != null) {
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
                    text = endLabel.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
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
