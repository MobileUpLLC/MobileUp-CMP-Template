package ru.mobileup.template.core.configuration

import androidx.compose.runtime.staticCompositionLocalOf

actual class Platform {
    actual val type: PlatformType = PlatformType.Ios
}

val LocalPlatform = staticCompositionLocalOf<Platform> { error("Platform is not set") }
