package ru.mobileup.template.core.map.internal

import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import ru.mobileup.template.core.location.GeoCoordinate
import ru.mobileup.template.core.map.MapCameraPosition

private const val ANIMATION_DURATION_SECONDS = 1.0f

internal class CameraController(
    private val mapView: MapView,
    private val onCameraPositionChange: (MapCameraPosition) -> Unit
) {

    private val cameraListener = CameraListener { _, position, _, _ ->
        onCameraPositionChange(position.toMapCameraPosition())
    }

    val cameraPosition: MapCameraPosition
        get() = mapView.mapWindow.map.cameraPosition.toMapCameraPosition()

    init {
        mapView.mapWindow.map.addCameraListener(cameraListener)
    }

    fun moveCamera(
        position: MapCameraPosition,
        animate: Boolean
    ) {
        mapView.mapWindow.map.moveCamera(position.toYandexCameraPosition(), animate)
    }

    fun calculateCameraPosition(coordinates: List<GeoCoordinate>): MapCameraPosition? {
        if (coordinates.isEmpty()) return null

        val geometry = Geometry.fromPolyline(Polyline(coordinates.map { it.toPoint() }))
        val currentPosition = mapView.mapWindow.map.cameraPosition
        val position = mapView.mapWindow.map.cameraPosition(
            geometry,
            null,
            currentPosition.azimuth,
            currentPosition.tilt
        )

        return MapCameraPosition(
            coordinate = position.target.toGeoCoordinate(),
            zoom = position.zoom,
            azimuth = position.azimuth,
            tilt = position.tilt
        )
    }
}

private fun Map.moveCamera(
    position: CameraPosition,
    animate: Boolean
) {
    if (animate) {
        move(position, Animation(Animation.Type.SMOOTH, ANIMATION_DURATION_SECONDS), null)
    } else {
        move(position)
    }
}

internal fun GeoCoordinate.toPoint(): Point = Point(latitude, longitude)

private fun Point.toGeoCoordinate(): GeoCoordinate = GeoCoordinate(latitude, longitude)

private fun MapCameraPosition.toYandexCameraPosition(): CameraPosition {
    return CameraPosition(coordinate.toPoint(), zoom, azimuth, tilt)
}

private fun CameraPosition.toMapCameraPosition(): MapCameraPosition {
    return MapCameraPosition(
        coordinate = target.toGeoCoordinate(),
        zoom = zoom,
        azimuth = azimuth,
        tilt = tilt
    )
}
