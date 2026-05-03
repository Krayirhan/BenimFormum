package com.krayirhan.benimformum.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Kart ve ızgara için teknik sabitler.
 * Aralık aileleri: [FormSpacing]; köşe: [FormRadius].
 *
 * Alt gezinme: [com.krayirhan.benimformum.navigation.AppNavHost] içindeki `Scaffold` içerik alanı
 * `padding(innerPadding)` ile zaten sistem + bottom bar inset’ini tüketir. Kaydırma sonuna eklenecek
 * tek ek pay [navContentExtra] olmalıdır (LazyColumn `contentPadding` veya `Spacer`).
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
    /**
     * Scaffold `innerPadding` uygulandıktan sonra listenin sonuna eklenecek nefes payı.
     * Inset henüz uygulanmıyorsa: `contentPadding(bottom = innerPadding.calculateBottomPadding() + navContentExtra)`.
     */
    val navContentExtra = 32.dp
}
