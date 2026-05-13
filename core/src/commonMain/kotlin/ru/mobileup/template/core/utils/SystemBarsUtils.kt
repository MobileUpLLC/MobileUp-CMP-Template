package ru.mobileup.template.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.util.fastFold
import androidx.compose.ui.util.fastMap
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

val LocalSystemBarsSettings = staticCompositionLocalOf {
    mutableStateListOf<Pair<String, SystemBarsSettings>>()
}

enum class SystemBarIconsColor {
    Light, Dark, Unspecified;

    @Stable
    inline val isSpecified: Boolean get() = this != Unspecified

    inline fun takeOrElse(block: () -> SystemBarIconsColor): SystemBarIconsColor =
        if (isSpecified) this else block()
}

/**
 * Represents the configuration settings for system bars, including status bar and navigation bar colors,
 * as well as icon colors for each.
 *
 * @property statusBarColor The color of the status bar. If set to [Color.Unspecified], the primary background
 * color from the theme is used, and an alpha value is applied dynamically based on its luminance.
 * @property navigationBarColor The color of the navigation bar. If set to [Color.Unspecified], the primary
 * background color from the theme is used, and an alpha value is applied dynamically based on its luminance.
 * @property statusBarIconsColor The color scheme for the status bar icons. If set to [SystemBarIconsColor.Unspecified],
 * the value is determined based on the app's theme: dark icons for light themes, light icons for dark themes.
 * @property navigationBarIconsColor The color scheme for the navigation bar icons. If set to [SystemBarIconsColor.Unspecified],
 * the value is determined based on the app's theme: dark icons for light themes, light icons for dark themes.
 */
data class SystemBarsSettings(
    val statusBarColor: Color,
    val navigationBarColor: Color,
    val statusBarIconsColor: SystemBarIconsColor,
    val navigationBarIconsColor: SystemBarIconsColor,
) {
    companion object {
        val DEFAULT = SystemBarsSettings(
            statusBarColor = Color.Unspecified,
            navigationBarColor = Color.Unspecified,
            statusBarIconsColor = SystemBarIconsColor.Unspecified,
            navigationBarIconsColor = SystemBarIconsColor.Unspecified,
        )
    }
}

fun List<Pair<String, SystemBarsSettings>>.accumulate() = fastMap { it.second }.fastFold(
    SystemBarsSettings.DEFAULT
) { acc, settings ->
    SystemBarsSettings(
        statusBarColor = settings.statusBarColor.takeOrElse(acc::statusBarColor),
        navigationBarColor = settings.navigationBarColor.takeOrElse(acc::navigationBarColor),
        statusBarIconsColor = settings.statusBarIconsColor.takeOrElse(acc::statusBarIconsColor),
        navigationBarIconsColor = settings.navigationBarIconsColor.takeOrElse(acc::navigationBarIconsColor),
    )
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun SystemBars(
    statusBarColor: Color = Color.Unspecified,
    navigationBarColor: Color = Color.Unspecified,
    statusBarIconsColor: SystemBarIconsColor = SystemBarIconsColor.Unspecified,
    navigationBarIconsColor: SystemBarIconsColor = SystemBarIconsColor.Unspecified,
) {
    val key = remember { Uuid.random().toString() }
    val newSystemBarsSettings = SystemBarsSettings(
        statusBarColor = statusBarColor,
        navigationBarColor = navigationBarColor,
        statusBarIconsColor = statusBarIconsColor,
        navigationBarIconsColor = navigationBarIconsColor,
    )
    val systemBarsSettings = LocalSystemBarsSettings.current

    DisposableEffect(newSystemBarsSettings) {
        val config = key to newSystemBarsSettings
        systemBarsSettings.add(config)
        onDispose { systemBarsSettings.remove(config) }
    }
}
