package ru.mobileup.template.core.map.internal

import ru.mobileup.template.core.map.MapCommand

private const val ZOOM_STEP = 1.0f

internal fun MapController.executeMapCommand(command: MapCommand) {
    when (command) {
        is MapCommand.MoveTo -> moveCamera(
            position = cameraPosition.copy(
                coordinate = command.coordinate,
                zoom = when {
                    command.zoom == null -> cameraPosition.zoom
                    command.dontZoomOut && command.zoom < cameraPosition.zoom -> cameraPosition.zoom
                    command.dontZoomIn && command.zoom > cameraPosition.zoom -> cameraPosition.zoom
                    else -> command.zoom
                }
            ),
            animate = command.animate
        )

        MapCommand.ZoomIn -> moveCamera(
            position = cameraPosition.copy(zoom = cameraPosition.zoom + ZOOM_STEP),
            animate = true
        )

        MapCommand.ZoomOut -> moveCamera(
            position = cameraPosition.copy(zoom = cameraPosition.zoom - ZOOM_STEP),
            animate = true
        )

        is MapCommand.MoveToBoundingBox -> {
            val position = calculateBoundingBoxCameraPosition(command.coordinates) ?: return
            val zoomedOutPosition = position.copy(zoom = position.zoom - command.zoomOutOffset)
            moveCamera(zoomedOutPosition, command.animate)
        }
    }
}
