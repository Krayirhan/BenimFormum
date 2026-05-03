package com.krayirhan.benimformum.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.krayirhan.benimformum.core.ui.Spacing
import com.krayirhan.benimformum.core.ui.rememberAppHaptics
import com.krayirhan.benimformum.ui.theme.appColors

@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    hapticOnClick: Boolean = true
) {
    val haptics = rememberAppHaptics()
    val appColors = MaterialTheme.appColors
    val scheme = MaterialTheme.colorScheme
    val interactive = enabled && !loading
    Button(
        onClick = {
            if (hapticOnClick) haptics.confirm()
            onClick()
        },
        modifier = modifier,
        enabled = interactive,
        colors = ButtonDefaults.buttonColors(
            containerColor = appColors.privacy,
            contentColor = appColors.onPrivacy,
            disabledContainerColor = scheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = scheme.onSurface.copy(alpha = 0.38f)
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(end = Spacing.sm)
                    .size(16.dp),
                strokeWidth = 2.dp,
                color = appColors.onPrivacy
            )
        } else if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = Spacing.sm)
                    .size(18.dp)
            )
        }
        Text(text)
    }
}

@Composable
fun SecondaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    hapticOnClick: Boolean = true,
    /** null = marka yeşili; su kartında yumuşak mavi ton için doldurulabilir. */
    borderColor: Color? = null,
    contentColor: Color? = null
) {
    val haptics = rememberAppHaptics()
    val appColors = MaterialTheme.appColors
    val scheme = MaterialTheme.colorScheme
    val interactive = enabled && !loading
    val resolvedBorder = borderColor ?: appColors.privacy
    val resolvedContent = contentColor ?: appColors.privacy
    OutlinedButton(
        onClick = {
            if (hapticOnClick) haptics.tap()
            onClick()
        },
        modifier = modifier,
        enabled = interactive,
        border = BorderStroke(
            width = 1.dp,
            color = if (interactive) resolvedBorder else scheme.outline.copy(alpha = 0.38f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = resolvedContent,
            disabledContentColor = scheme.onSurface.copy(alpha = 0.38f)
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(end = Spacing.sm)
                    .size(16.dp),
                strokeWidth = 2.dp,
                color = resolvedContent
            )
        } else if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = Spacing.sm)
                    .size(18.dp)
            )
        }
        Text(text)
    }
}
