package com.krayirhan.benimformum.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Köşe yarıçapları — PPTX kartlarındaki yumuşak dikdörtgenlere yakın, biraz daha geniş "defter" köşesi.
 */
object AppShapes {
    val extraSmall = RoundedCornerShape(4.dp)
    val small = RoundedCornerShape(8.dp)
    /** Metin alanları, küçük rozetler. */
    val medium = RoundedCornerShape(14.dp)
    /** Üst bölüm / geniş kartlar. */
    val large = RoundedCornerShape(20.dp)

    /** Kartlar, metrik kutuları, içgörü panelleri. */
    val card = FormRadius.cardShape
}
