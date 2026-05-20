package ru.mobileup.template.core.widget.switch

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun AppSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: AppSwitchColors = AppSwitchDefaults.colors(),
)
