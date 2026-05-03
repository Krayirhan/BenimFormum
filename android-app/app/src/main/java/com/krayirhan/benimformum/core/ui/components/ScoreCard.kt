package com.krayirhan.benimformum.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.krayirhan.benimformum.core.ui.Spacing
import com.krayirhan.benimformum.ui.theme.NumericDisplayMedium
import com.krayirhan.benimformum.ui.theme.appColors

@Composable
fun scoreColor(score: Int): Color = when {
    score <= 0 -> MaterialTheme.colorScheme.onSurfaceVariant
    score <= 40 -> MaterialTheme.appColors.scoreLow
    score <= 70 -> MaterialTheme.appColors.scoreMid
    else -> MaterialTheme.appColors.scoreHigh
}

@Composable
fun ScoreCard(
    score: Int,
    modifier: Modifier = Modifier
) {
    val safeScore = score.coerceIn(0, 100)
    val animatedScore by animateIntAsState(
        targetValue = safeScore,
        animationSpec = tween(durationMillis = 700),
        label = "scoreValue"
    )
    val targetColor = scoreColor(safeScore)
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 700),
        label = "scoreColor"
    )
    val progress = animatedScore / 100f
    val helper = when {
        safeScore <= 0 -> "Bugünkü kaydını eklediğinde skorun burada görünecek."
        safeScore <= 40 -> "Sakin başlangıç. Su ve uykuya küçük dokunuşlar yeter."
        safeScore <= 70 -> "Dengeli ilerliyorsun. Bir adım daha mümkün."
        else -> "Bugün kendine güzel davranıyorsun."
    }
    val band = when {
        safeScore <= 0 -> "—"
        safeScore <= 40 -> "Hafif"
        safeScore <= 70 -> "Orta"
        else -> "İyi"
    }

    AppCard(modifier = modifier, style = AppCardStyle.Hero) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Box(
                modifier = Modifier.size(96.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(96.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 8.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(96.dp),
                    color = animatedColor,
                    strokeWidth = 8.dp,
                    trackColor = Color.Transparent
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = animatedScore.toString(),
                        style = NumericDisplayMedium,
                        color = animatedColor
                    )
                    Text(
                        text = "/ 100",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Günlük form skoru",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .padding(top = Spacing.xs)
                        .clip(RoundedCornerShape(999.dp))
                        .background(animatedColor.copy(alpha = 0.18f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = band,
                        style = MaterialTheme.typography.labelLarge,
                        color = animatedColor
                    )
                }
                Text(
                    text = helper,
                    modifier = Modifier.padding(top = Spacing.sm),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
