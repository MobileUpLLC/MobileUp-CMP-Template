package ru.mobileup.template.core.map.internal

import androidx.compose.runtime.saveable.Saver
import ru.mobileup.template.core.location.GeoCoordinate
import ru.mobileup.template.core.map.MapCameraPosition

internal val MapCameraPositionSaver = Saver<MapCameraPosition, List<Double>>(
    save = { position ->
        listOf(
            position.coordinate.latitude,
            position.coordinate.longitude,
            position.zoom.toDouble(),
            position.azimuth.toDouble(),
            position.tilt.toDouble()
        )
    },
    restore = { values ->
        MapCameraPosition(
            coordinate = GeoCoordinate(values[0], values[1]),
            zoom = values[2].toFloat(),
            azimuth = values[3].toFloat(),
            tilt = values[4].toFloat()
        )
    }
)
