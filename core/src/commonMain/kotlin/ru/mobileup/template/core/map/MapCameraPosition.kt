package ru.mobileup.template.core.map

import kotlinx.serialization.Serializable
import ru.mobileup.template.core.location.GeoCoordinate

@Serializable
data class MapCameraPosition(
    val coordinate: GeoCoordinate,
    val zoom: Float,
    val azimuth: Float = 0f,
    val tilt: Float = 0f
) {
    companion object {
        const val DEFAULT_MAP_ZOOM = 6f
        const val CLOSE_MAP_ZOOM = 16f

        val DEFAULT = MapCameraPosition(
            coordinate = GeoCoordinate.KREMLIN,
            zoom = DEFAULT_MAP_ZOOM
        )
    }
}