package ru.mobileup.template.core.map

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.mobileup.template.core.map.internal.MapCameraPositionSaver
import ru.mobileup.template.core.map.internal.MapController
import ru.mobileup.template.core.map.internal.MapLogoHorizontalAlignment
import ru.mobileup.template.core.map.internal.MapLogoPosition
import ru.mobileup.template.core.map.internal.MapLogoVerticalAlignment
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

    val density = LocalDensity.current
    val statusBarTopPx = WindowInsets.statusBars.getTop(density)
    val defaultLogoPaddingPx = remember(density) {
        with(density) { 8.dp.roundToPx() }
    }
    val logoPosition = remember(statusBarTopPx, defaultLogoPaddingPx) {
        MapLogoPosition(
            horizontalAlignment = MapLogoHorizontalAlignment.Right,
            verticalAlignment = MapLogoVerticalAlignment.Top,
            horizontalPaddingPx = defaultLogoPaddingPx,
            verticalPaddingPx = statusBarTopPx + defaultLogoPaddingPx
        )
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

    LaunchedEffect(mapController, logoPosition) {
        mapController?.setLogoPosition(logoPosition)
    }

    LaunchedEffect(mapController, mapCommands) {
        val controller = mapController ?: return@LaunchedEffect
        mapCommands.collect { command ->
            controller.executeMapCommand(command)
        }
    }
}
