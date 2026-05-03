package com.krayirhan.benimformum.core.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

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
        Snackbar(snackbarData = data)
    }
}
