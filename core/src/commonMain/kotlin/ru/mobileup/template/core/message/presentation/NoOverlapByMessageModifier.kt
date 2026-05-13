package ru.mobileup.template.core.message.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.max
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

val LocalMessageOffsets = staticCompositionLocalOf { mutableStateMapOf<String, Int>() }

/**
 * Allows to specify that a message (see [MessageUi]) should be displayed above an UI element.
 * For example, set noOverlapByMessage() to a bottom navigation bar, so it will not be overlapped by a message popup.
 */
@OptIn(ExperimentalUuidApi::class)
fun Modifier.noOverlapByMessage(): Modifier = composed {
    val key = remember { Uuid.random().toString() }
    val localMessageOffsets = LocalMessageOffsets.current

    val bottomInset = with(LocalDensity.current) {
        val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val imePadding = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
        max(navigationBarsPadding, imePadding).toPx()
    }

    DisposableEffect(Unit) {
        onDispose {
            localMessageOffsets.remove(key)
        }
    }
    onGloballyPositioned { layoutCoordinates ->
        if (layoutCoordinates.isAttached) {
            val y = layoutCoordinates.positionInRoot().y
            val rootHeight = layoutCoordinates.findRootCoordinates().size.height
            val offset = (rootHeight - bottomInset - y).toInt().coerceAtLeast(0)
            localMessageOffsets[key] = offset
        }
    }
}
