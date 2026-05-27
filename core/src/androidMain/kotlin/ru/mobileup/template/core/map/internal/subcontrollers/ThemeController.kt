package ru.mobileup.template.core.map.internal.subcontrollers

import com.yandex.mapkit.mapview.MapView
import ru.mobileup.template.core.map.MapTheme

internal class ThemeController(
    private val mapView: MapView
) {
    fun setTheme(theme: MapTheme) {
        mapView.mapWindow.map.setMapStyle(theme.yandexMapStyleJson)
    }
}
