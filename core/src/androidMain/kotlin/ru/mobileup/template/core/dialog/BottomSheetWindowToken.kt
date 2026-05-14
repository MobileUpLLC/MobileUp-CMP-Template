package ru.mobileup.template.core.dialog

import android.os.IBinder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// Store window tokens to show MessageUi Popup over Bottom sheet dialog on Android
internal val LocalBottomSheetWindowTokens = staticCompositionLocalOf {
    mutableStateMapOf<String, IBinder>()
}

@Composable
@OptIn(ExperimentalUuidApi::class)
internal actual fun RegisterBottomSheetWindowToken() {
    val view = LocalView.current
    val tokens = LocalBottomSheetWindowTokens.current
    val key = remember { Uuid.random().toString() }

    DisposableEffect(view, tokens, key) {
        val token = view.applicationWindowToken
        if (token != null) {
            tokens[key] = token
        }

        onDispose {
            tokens.remove(key)
        }
    }
}
