package ru.mobileup.template.core.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.layout.windowInsetsStartWidth
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import ru.mobileup.template.core.theme.custom.CustomTheme

/**
 * Should be implemented by platforms and provided through LocalSystemBarIconsColorHandler
 */
interface SystemBarIconsColorHandler {
    fun updateSystemBarIconsColor(darkStatusBarIcons: Boolean, darkNavigationBarIcons: Boolean)
}

val LocalSystemBarIconsColorHandler = staticCompositionLocalOf<SystemBarIconsColorHandler?> { null }

private enum class NavBarPosition {
    Bottom, Left, Right, None
}

/**
 * Configures system bar colors while adapting to different navigation modes.
 *
 * This function dynamically adjusts the system bars (status bar and navigation bar)
 * based on the current navigation mode, ensuring correct background colors.
 *
 * Supports screen rotation and correctly handles the navigation bar in both portrait and landscape orientations.
 *
 * Should be called once at root ui **after** the main content, not before it.
 */
@Suppress("ModifierMissing")
@Composable
fun ConfigureSystemBars(settings: SystemBarsSettings) {
    val systemGestures = WindowInsets.systemGestures
    val navigationBars = WindowInsets.navigationBars
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val backgroundColor = CustomTheme.colors.background.screen
    val isLightTheme = CustomTheme.colors.isLight

    val isGestureNavigation by remember {
        derivedStateOf {
            systemGestures.run {
                getLeft(density, layoutDirection) > 0 && getRight(density, layoutDirection) > 0
            }
        }
    }
    val navBarPosition by remember {
        derivedStateOf {
            when {
                navigationBars.getBottom(density) > 0 -> NavBarPosition.Bottom
                navigationBars.getLeft(density, layoutDirection) > 0 -> NavBarPosition.Left
                navigationBars.getRight(density, layoutDirection) > 0 -> NavBarPosition.Right
                else -> NavBarPosition.None
            }
        }
    }

    val defaultStatusBarColor = Color.Transparent
    val statusBarColor = remember(settings, defaultStatusBarColor) {
        settings.statusBarColor.takeOrElse { defaultStatusBarColor }
    }

    val defaultNavBarColor = remember(backgroundColor) {
        backgroundColor.copy(alpha = 0.5f)
    }
    val navBarColor = remember(settings, defaultNavBarColor, isGestureNavigation) {
        if (isGestureNavigation) {
            Color.Transparent
        } else {
            settings.navigationBarColor.takeOrElse { defaultNavBarColor }
        }
    }

    val darkStatusBarIcons = remember(settings.statusBarIconsColor, isLightTheme) {
        when (settings.statusBarIconsColor) {
            SystemBarIconsColor.Dark -> true
            SystemBarIconsColor.Light -> false
            SystemBarIconsColor.Unspecified -> isLightTheme
        }
    }
    val darkNavigationBarIcons = remember(settings.navigationBarIconsColor, isLightTheme) {
        when (settings.navigationBarIconsColor) {
            SystemBarIconsColor.Dark -> true
            SystemBarIconsColor.Light -> false
            SystemBarIconsColor.Unspecified -> isLightTheme
        }
    }

    val systemBarIconsColorHandler = LocalSystemBarIconsColorHandler.current
    if (systemBarIconsColorHandler != null) {
        LaunchedEffect(darkStatusBarIcons, darkNavigationBarIcons) {
            systemBarIconsColorHandler.updateSystemBarIconsColor(
                darkStatusBarIcons,
                darkNavigationBarIcons
            )
        }
    }

    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .drawBehind {
                    drawRect(statusBarColor)
                }
        )

        val navBarModifier = remember(navBarPosition) {
            when (navBarPosition) {
                NavBarPosition.Bottom -> Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(navigationBars)

                NavBarPosition.Left -> Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .windowInsetsStartWidth(navigationBars)

                NavBarPosition.Right -> Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .windowInsetsEndWidth(navigationBars)

                NavBarPosition.None -> Modifier
            }
        }

        Box(
            navBarModifier.drawBehind {
                drawRect(navBarColor)
            }
        )
    }
}
