package ru.mobileup.template.core.theme.custom

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

fun CustomColors.toMaterialColors(): ColorScheme {
    return if (isLight) {
        lightColorScheme(
            primary = button.primary,
            onPrimary = text.invert,
            background = background.screen,
            surface = background.screen,
        )
    } else {
        darkColorScheme(
            primary = button.primary,
            onPrimary = text.invert,
            background = background.screen,
            surface = background.screen,
        )
    }
}

fun CustomTypography.toMaterialTypography(): Typography {
    return Typography(
        titleLarge = title.regular,
        titleMedium = title.regular,
        titleSmall = title.regular,
        bodyLarge = body.regular,
        bodyMedium = body.regular,
        bodySmall = body.regular,
        labelLarge = caption.regular,
        labelMedium = caption.regular,
        labelSmall = caption.regular
    )
}
