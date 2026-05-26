package ru.mobileup.template.core.configuration

import androidx.compose.runtime.staticCompositionLocalOf
import ru.mobileup.template.core.map.internal.IosMapControllerFactory

actual class Platform(
    val iosMapControllerFactory: IosMapControllerFactory
) {
    actual val type: PlatformType = PlatformType.Ios
}

val LocalPlatform = staticCompositionLocalOf<Platform> { error("Platform is not set") }
