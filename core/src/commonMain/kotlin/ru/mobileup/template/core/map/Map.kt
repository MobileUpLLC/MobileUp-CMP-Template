package ru.mobileup.template.core.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.mobileup.template.core.map.internal.MapCameraPositionSaver
import ru.mobileup.template.core.map.internal.MapController
import ru.mobileup.template.core.map.internal.PlatformMap
import ru.mobileup.template.core.map.internal.executeMapCommand

@Composable
fun Map(
    onMarkerClick: (MapMarker) -> Unit,
    onClusterClick: (List<MapMarker>) -> Unit,
    showCurrentLocationMarker: Boolean,
    markers: List<MapMarker>,
    modifier: Modifier = Modifier,
    initialCameraPosition: MapCameraPosition = MapCameraPosition.DEFAULT,
    mapCommands: Flow<MapCommand> = emptyFlow(),
    theme: MapTheme = MapTheme.Default,
) {
    var savedCameraPosition by rememberSaveable(stateSaver = MapCameraPositionSaver) {
        mutableStateOf(initialCameraPosition)
    }

    var mapController by remember { mutableStateOf<MapController?>(null) }
    PlatformMap(
        modifier = modifier,
        onMapReady = { controller ->
            controller.moveCamera(savedCameraPosition, animate = false)
            mapController = controller
        },
        onCameraPositionChange = { savedCameraPosition = it },
        onMarkerClick = onMarkerClick,
        onClusterClick = onClusterClick
    )

    LaunchedEffect(mapController, markers) {
        mapController?.setMarkers(markers)
    }

    LaunchedEffect(mapController, showCurrentLocationMarker) {
        mapController?.setCurrentLocationMarkerVisible(showCurrentLocationMarker)
    }

    LaunchedEffect(mapController, theme) {
        mapController?.setTheme(theme)
    }

    LaunchedEffect(mapController, mapCommands) {
        val controller = mapController ?: return@LaunchedEffect
        mapCommands.collect { command ->
            controller.executeMapCommand(command)
        }
    }
}
