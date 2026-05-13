package ru.mobileup.template.core.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

// TODO: remove when `PaddingValues.plus` will be released in Compose Multiplatform
@Stable
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues =
    object : PaddingValues {
        override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
            this@plus.calculateLeftPadding(layoutDirection) +
                    other.calculateLeftPadding(layoutDirection)

        override fun calculateTopPadding(): Dp =
            this@plus.calculateTopPadding() + other.calculateTopPadding()

        override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
            this@plus.calculateRightPadding(layoutDirection) +
                    other.calculateRightPadding(layoutDirection)

        override fun calculateBottomPadding(): Dp =
            this@plus.calculateBottomPadding() + other.calculateBottomPadding()
    }

@Composable
fun PaddingValues.withImePadding(): PaddingValues {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val imeBottom = with(density) {
        WindowInsets.ime.getBottom(this).toDp()
    }

    val start = calculateStartPadding(layoutDirection)
    val top = calculateTopPadding()
    val end = calculateEndPadding(layoutDirection)
    val bottom = calculateBottomPadding()

    return PaddingValues(
        start = start,
        top = top,
        end = end,
        bottom = maxOf(bottom, imeBottom)
    )
}
