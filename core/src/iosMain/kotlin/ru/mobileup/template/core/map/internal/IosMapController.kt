package ru.mobileup.template.core.map.internal

import platform.UIKit.UIView
import ru.mobileup.template.core.map.MapCameraPosition
import ru.mobileup.template.core.map.MapMarker

// Implemented in Swift
interface IosMapController : MapController {
    val view: UIView
    fun dispose()
}

// Implemented in Swift
interface IosMapControllerFactory {
    fun create(
        onCameraPositionChange: (MapCameraPosition) -> Unit,
        onMarkerClick: (MapMarker) -> Unit,
        onClusterClick: (List<MapMarker>) -> Unit
    ): IosMapController
}
