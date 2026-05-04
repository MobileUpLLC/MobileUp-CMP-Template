package ru.mobileup.template.core.configuration

import androidx.compose.runtime.staticCompositionLocalOf

enum class PlatformType {
    Android,
    Ios
}

val LocalPlatformType = staticCompositionLocalOf { PlatformType.Android }

expect class Platform {
    val type: PlatformType
}
