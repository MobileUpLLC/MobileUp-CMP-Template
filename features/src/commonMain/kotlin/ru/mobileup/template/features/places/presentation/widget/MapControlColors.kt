package ru.mobileup.template.features.places.presentation.widget

import androidx.compose.ui.graphics.Color
import ru.mobileup.template.core.map.MapTheme

internal data class MapControlColors(
    val background: Color,
    val selectedBackground: Color,
    val border: Color,
    val icon: Color,
)

internal fun mapControlColors(theme: MapTheme): MapControlColors {
    return when (theme) {
        MapTheme.Dark -> MapControlColors(
            background = Color(0xFF2C2C2C).copy(alpha = 0.8f),
            selectedBackground = Color.White.copy(alpha = 0.3f),
            border = Color(0xFFB6B6B6),
            icon = Color.White
        )
        MapTheme.Default -> MapControlColors(
            background = Color(0xFFEFEFEF).copy(alpha = 0.8f),
            selectedBackground = Color.Black.copy(alpha = 0.2f),
            border = Color(0xFF909090),
            icon = Color.Black
        )
        MapTheme.Bright -> MapControlColors(
            background = Color.White.copy(alpha = 0.8f),
            selectedBackground = Color.Black.copy(alpha = 0.2f),
            border = Color(0xFF909090),
            icon = Color.Black
        )
    }
}
