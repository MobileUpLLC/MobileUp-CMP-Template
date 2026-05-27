package ru.mobileup.template.core.map.internal

import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView
import ru.mobileup.template.core.location.GeoCoordinate
import ru.mobileup.template.core.map.MapCameraPosition
import ru.mobileup.template.core.map.MapMarker
import ru.mobileup.template.core.map.MapTheme

internal class AndroidMapController(
    private val mapView: MapView,
    onCameraPositionChange: (MapCameraPosition) -> Unit,
    onMarkerClick: (MapMarker) -> Unit,
    onClusterClick: (List<MapMarker>) -> Unit
) : MapController {

    private val cameraController = CameraController(
        mapView = mapView,
        onCameraPositionChange = onCameraPositionChange
    )

    private val markersController = MarkersController(
        mapView = mapView,
        onMarkerClick = onMarkerClick,
        onClusterClick = onClusterClick
    )

    private val currentLocationMarkerController = CurrentLocationMarkerController(mapView)

    private val themeController = ThemeController(mapView)

    private val logoController = LogoController(mapView)

    override val cameraPosition: MapCameraPosition
        get() = cameraController.cameraPosition

    fun start() {
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    fun stop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun moveCamera(
        position: MapCameraPosition,
        animate: Boolean
    ) {
        cameraController.moveCamera(position, animate)
    }

    override fun calculateBoundingBoxCameraPosition(
        coordinates: List<GeoCoordinate>
    ): MapCameraPosition? {
        return cameraController.calculateCameraPosition(coordinates)
    }

    override fun setMarkers(markers: List<MapMarker>) {
        markersController.setMarkers(markers)
    }

    override fun setCurrentLocationMarkerVisible(isVisible: Boolean) {
        currentLocationMarkerController.setVisible(isVisible)
    }

    override fun setTheme(theme: MapTheme) {
        themeController.setTheme(theme)
    }

    override fun setLogoPosition(position: MapLogoPosition) {
        logoController.setPosition(position)
    }
}
