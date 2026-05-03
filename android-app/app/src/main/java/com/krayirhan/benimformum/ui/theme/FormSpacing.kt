package com.krayirhan.benimformum.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Form ekranları için aralıklar.
 *
 * **Alt kaydırma:** Ana sekmelerde `Scaffold { innerPadding -> … .padding(innerPadding) }` ile inset
 * zaten uygulanır; bu durumda [scrollContentTail] yeterlidir.
 * İçerik tam pencereye yayılıp inset almıyorsa [scrollLazyBottomPadding] kullanın.
 */
object FormSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val section = FormTokens.sectionSpacing
    val gridGap = FormTokens.gridGap

    /** Scaffold alt inset sonrası liste/scroll sonu nefes payı. */
    val scrollContentTail: Dp = FormTokens.navContentExtra

    val navContentExtra: Dp = FormTokens.navContentExtra

    /**
     * `LazyColumn` / `Modifier.padding` için: alttan inset uygulanmıyorsa
     * `bottom = scaffoldBottomInset + navContentExtra`.
     */
    fun scrollLazyBottomPadding(scaffoldBottomInset: Dp): PaddingValues =
        PaddingValues(bottom = scaffoldBottomInset + navContentExtra)
}
