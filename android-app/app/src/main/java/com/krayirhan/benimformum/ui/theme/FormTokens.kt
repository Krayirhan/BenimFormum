package com.krayirhan.benimformum.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Kart ve ızgara için teknik sabitler.
 * Aralık aileleri: [FormSpacing]; köşe: [FormRadius].
 */
object FormTokens {
    val cardInnerHorizontal = 20.dp
    val cardInnerVertical = 20.dp
    val metricTilePaddingHorizontal = 18.dp
    val metricTilePaddingVertical = 12.dp
    val metricTileMinHeight = 100.dp
    val sectionSpacing = 24.dp
    val gridGap = 14.dp
    val cardBorderWidth = 1.dp
    val cardCorner = FormRadius.card
    /** Alt gezinme + rahat kaydırma (içerik kesilmesini önlemek için). */
    val scrollBottomComfort = 112.dp
    /** innerPadding.bottom’a eklenecek ek nefes. */
    val navContentExtra = 32.dp
}
