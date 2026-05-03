package com.krayirhan.benimformum.ui.theme

import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/** Material You açık olsa da çip seçimi çelik marka paletinde kalır. */
@Composable
fun brandFilterChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = MaterialTheme.colorScheme.surface,
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        selectedContainerColor = MaterialTheme.appColors.privacyContainer,
    selectedLabelColor = MaterialTheme.appColors.privacy,
    selectedLeadingIconColor = MaterialTheme.appColors.privacy,
    selectedTrailingIconColor = MaterialTheme.appColors.privacy
)
