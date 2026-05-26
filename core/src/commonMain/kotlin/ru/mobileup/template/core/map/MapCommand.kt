package ru.mobileup.template.core.map

import ru.mobileup.template.core.location.GeoCoordinate

sealed interface MapCommand {

    data class MoveTo(
        val coordinate: GeoCoordinate,
        val zoom: Float? = null, // null means don't change
        val animate: Boolean = true,
        val dontZoomOut: Boolean = false, // don't change zoom if requested zoom is lower than current
        val dontZoomIn: Boolean = false // don't change zoom if requested zoom is higher than current
    ) : MapCommand

    data object ZoomIn : MapCommand

    data object ZoomOut : MapCommand

    data class MoveToBoundingBox(
        val coordinates: List<GeoCoordinate>,
        val animate: Boolean = true,
        val zoomOutOffset: Float = 0.3f
    ) : MapCommand
}
