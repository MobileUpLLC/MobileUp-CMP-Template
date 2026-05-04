package ru.mobileup.template.core.utils

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Should be used to handle clicks on toolbar back button.
 *
 * Usage:
 * onClick = LocalBackAction.current
 */

typealias BackAction = () -> Unit

val LocalBackAction = staticCompositionLocalOf<BackAction> { {} }
