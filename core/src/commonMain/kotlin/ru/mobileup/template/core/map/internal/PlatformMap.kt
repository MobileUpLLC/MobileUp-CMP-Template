package ru.mobileup.template.core.map.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.mobileup.template.core.map.MapCameraPosition
import ru.mobileup.template.core.map.MapMarker

@Composable
internal expect fun PlatformMap(
    onMapReady: (MapController) -> Unit,
    onCameraPositionChange: (MapCameraPosition) -> Unit,
    onMarkerClick: (MapMarker) -> Unit,
    onClusterClick: (List<MapMarker>) -> Unit,
    modifier: Modifier = Modifier
)
