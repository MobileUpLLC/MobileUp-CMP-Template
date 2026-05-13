package ru.mobileup.template.core.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Displays a loading indicator on top of content when data is refreshing.
 */
@Composable
expect fun RefreshingProgress(visible: Boolean, modifier: Modifier = Modifier)