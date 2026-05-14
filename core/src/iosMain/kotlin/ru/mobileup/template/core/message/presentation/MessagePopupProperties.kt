package ru.mobileup.template.core.message.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.PopupProperties

@Composable
internal actual fun messagePopupProperties(): PopupProperties = PopupProperties(
    dismissOnBackPress = false,
    dismissOnClickOutside = false
)
