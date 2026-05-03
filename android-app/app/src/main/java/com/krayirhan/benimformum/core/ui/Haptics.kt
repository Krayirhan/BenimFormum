package com.krayirhan.benimformum.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

class AppHaptics internal constructor(private val haptic: HapticFeedback) {
    fun tap() {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun confirm() {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
}

@Composable
fun rememberAppHaptics(): AppHaptics {
    val haptic = LocalHapticFeedback.current
    return remember(haptic) { AppHaptics(haptic) }
}
