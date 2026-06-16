package ru.mobileup.template.core.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun FullscreenCircularProgress(
    modifier: Modifier = Modifier,
    overlay: Boolean = false
)