package ru.mobileup.template.core.message.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.PopupProperties
import ru.mobileup.template.core.dialog.LocalBottomSheetWindowTokens

@Composable
internal actual fun messagePopupProperties(): PopupProperties = PopupProperties(
    dismissOnBackPress = false,
    dismissOnClickOutside = false,
    windowToken = LocalBottomSheetWindowTokens.current.values.lastOrNull()
)
