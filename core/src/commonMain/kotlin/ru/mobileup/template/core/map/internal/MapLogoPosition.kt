package ru.mobileup.template.core.map.internal

data class MapLogoPosition(
    val horizontalAlignment: MapLogoHorizontalAlignment,
    val verticalAlignment: MapLogoVerticalAlignment,
    val horizontalPaddingPx: Int,
    val verticalPaddingPx: Int
)

enum class MapLogoHorizontalAlignment {
    Left,
    Right
}

enum class MapLogoVerticalAlignment {
    Top,
    Bottom
}
