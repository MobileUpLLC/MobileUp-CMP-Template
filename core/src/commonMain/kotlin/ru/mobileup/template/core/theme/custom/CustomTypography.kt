package ru.mobileup.template.core.theme.custom

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

data class CustomTypography(
    val title: TitleTypography,
    val body: BodyTypography,
    val caption: CaptionTypography,
    val button: ButtonTypography
)

data class TitleTypography(
    val regular: TextStyle
)

data class BodyTypography(
    val regular: TextStyle
)

data class CaptionTypography(
    val regular: TextStyle
)

data class ButtonTypography(
    val regular: TextStyle,
    val bold: TextStyle
)

val LocalCustomTypography = staticCompositionLocalOf<CustomTypography?> { null }
