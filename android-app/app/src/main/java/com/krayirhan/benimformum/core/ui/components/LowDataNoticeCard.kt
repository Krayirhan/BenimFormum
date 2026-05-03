package com.krayirhan.benimformum.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.krayirhan.benimformum.core.ui.Spacing

/**
 * Grafik veya trend için veri az olduğunda boş grafik yerine gösterilen kısa bilgi kartı.
 */
@Composable
fun LowDataNoticeCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        style = AppCardStyle.Insight
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = body,
                modifier = Modifier.padding(top = Spacing.xs),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
