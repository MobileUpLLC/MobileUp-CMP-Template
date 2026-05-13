package ru.mobileup.template.core.utils

import android.view.Window
import androidx.core.view.WindowInsetsControllerCompat

class AndroidSystemBarIconsColorHandler(
    private val window: Window
) : SystemBarIconsColorHandler {
    override fun updateSystemBarIconsColor(
        darkStatusBarIcons: Boolean,
        darkNavigationBarIcons: Boolean
    ) {
        WindowInsetsControllerCompat(window, window.decorView).run {
            isAppearanceLightStatusBars = darkStatusBarIcons
            isAppearanceLightNavigationBars = darkNavigationBarIcons
        }
    }
}