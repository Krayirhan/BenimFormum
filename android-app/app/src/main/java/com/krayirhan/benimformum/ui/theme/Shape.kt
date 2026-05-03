package com.krayirhan.benimformum.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Tek kaynaklı köşe yuvarlaklığı (Sprint 2 — S2-1).
 * Kartlar ve Bugün metrik kutuları aynı `card` köşesini kullanır.
 */
object AppShapes {
    val extraSmall = RoundedCornerShape(4.dp)
    val small = RoundedCornerShape(8.dp)
    val medium = RoundedCornerShape(12.dp)
    val large = RoundedCornerShape(16.dp)

    /** Kartlar, içgörü panelleri, Bugün metrik kutuları — biraz daha yumuşak premium köşe. */
    val card = medium
}
