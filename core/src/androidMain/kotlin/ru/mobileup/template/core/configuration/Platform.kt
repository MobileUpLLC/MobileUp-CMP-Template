package ru.mobileup.template.core.configuration

import android.app.Application
import ru.mobileup.template.core.debug_tools.AndroidDebugTools

actual class Platform(
    val application: Application,
    val debugTools: AndroidDebugTools
) {
    actual val type = PlatformType.Android
}