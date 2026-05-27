package ru.mobileup.template.core.map.internal

import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.logo.Alignment as LogoAlignment

internal class LogoController(
    private val mapView: MapView
) {
    fun setPosition(position: MapLogoPosition) {
        mapView.mapWindow.map.logo.setAlignment(
            LogoAlignment(
                position.horizontalAlignment.toYandexAlignment(),
                position.verticalAlignment.toYandexAlignment()
            )
        )
        mapView.mapWindow.map.logo.setPadding(
            Padding(
                position.horizontalPaddingPx,
                position.verticalPaddingPx
            )
        )
    }

    private fun MapLogoHorizontalAlignment.toYandexAlignment(): HorizontalAlignment {
        return when (this) {
            MapLogoHorizontalAlignment.Left -> HorizontalAlignment.LEFT
            MapLogoHorizontalAlignment.Right -> HorizontalAlignment.RIGHT
        }
    }

    private fun MapLogoVerticalAlignment.toYandexAlignment(): VerticalAlignment {
        return when (this) {
            MapLogoVerticalAlignment.Top -> VerticalAlignment.TOP
            MapLogoVerticalAlignment.Bottom -> VerticalAlignment.BOTTOM
        }
    }
}
