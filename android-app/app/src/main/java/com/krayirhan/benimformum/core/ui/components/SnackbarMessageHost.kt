package com.krayirhan.benimformum.core.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.krayirhan.benimformum.ui.theme.appColors

@Composable
fun rememberSnackbarHostState(): SnackbarHostState = remember { SnackbarHostState() }

@Composable
fun ObserveMessage(
    message: String?,
    snackbarHostState: SnackbarHostState,
    onConsumed: () -> Unit
) {
    LaunchedEffect(message) {
        val current = message
        if (!current.isNullOrBlank()) {
            snackbarHostState.showSnackbar(current)
            onConsumed()
        }
    }
}

@Composable
fun AppSnackbarHost(
    state: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = state,
        modifier = modifier
    ) { data ->
        val appColors = MaterialTheme.appColors
        val scheme = MaterialTheme.colorScheme
        Snackbar(
            snackbarData = data,
            containerColor = appColors.insightCardFill,
            contentColor = scheme.onSurface,
            actionColor = appColors.privacy,
            dismissActionContentColor = scheme.onSurfaceVariant
        )
    }
}
