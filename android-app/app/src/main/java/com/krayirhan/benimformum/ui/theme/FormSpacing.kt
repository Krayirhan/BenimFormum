package com.krayirhan.benimformum.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Form ekranları için aralıklar; alt gezinme üstü kaydırma payı dahil.
 */
object FormSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val section = FormTokens.sectionSpacing
    val gridGap = FormTokens.gridGap
    val bottomScrollComfort = FormTokens.scrollBottomComfort
    val navContentExtra = FormTokens.navContentExtra

    /** Scaffold alt inset + ekstra nefes (LazyColumn / scroll sonu). */
    fun scrollBottomWithScaffold(scaffoldBottom: Dp): Dp = scaffoldBottom + navContentExtra

    fun scrollContentPaddingValues(scaffoldBottom: Dp): PaddingValues =
        PaddingValues(bottom = scrollBottomWithScaffold(scaffoldBottom))
}
