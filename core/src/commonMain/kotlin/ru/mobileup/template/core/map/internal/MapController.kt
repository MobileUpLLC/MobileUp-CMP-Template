package ru.mobileup.template.core.map.internal

import ru.mobileup.template.core.location.GeoCoordinate
import ru.mobileup.template.core.map.MapCameraPosition
import ru.mobileup.template.core.map.MapMarker
import ru.mobileup.template.core.map.MapTheme

interface MapController {

    val cameraPosition: MapCameraPosition

    fun moveCamera(position: MapCameraPosition, animate: Boolean)

    fun calculateBoundingBoxCameraPosition(coordinates: List<GeoCoordinate>): MapCameraPosition?

    fun setMarkers(markers: List<MapMarker>)

    fun setCurrentLocationMarkerVisible(isVisible: Boolean)

    fun setTheme(theme: MapTheme)
}
