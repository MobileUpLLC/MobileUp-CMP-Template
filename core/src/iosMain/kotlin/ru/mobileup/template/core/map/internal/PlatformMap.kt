package ru.mobileup.template.core.map.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import ru.mobileup.template.core.configuration.LocalPlatform
import ru.mobileup.template.core.map.MapCameraPosition
import ru.mobileup.template.core.map.MapMarker

@Composable
internal actual fun PlatformMap(
    onMapReady: (MapController) -> Unit,
    onCameraPositionChange: (MapCameraPosition) -> Unit,
    onMarkerClick: (MapMarker) -> Unit,
    onClusterClick: (List<MapMarker>) -> Unit,
    modifier: Modifier
) {
    val platform = LocalPlatform.current

    val currentOnCameraPositionChange by rememberUpdatedState(onCameraPositionChange)
    val currentOnMarkerClick by rememberUpdatedState(onMarkerClick)
    val currentOnClusterClick by rememberUpdatedState(onClusterClick)
    val currentOnMapReady by rememberUpdatedState(onMapReady)

    val mapController = remember(platform.iosMapControllerFactory) {
        platform.iosMapControllerFactory.create(
            onCameraPositionChange = { currentOnCameraPositionChange(it) },
            onMarkerClick = { currentOnMarkerClick(it) },
            onClusterClick = { currentOnClusterClick(it) }
        )
    }

    DisposableEffect(mapController) {
        currentOnMapReady(mapController)

        onDispose {
            mapController.dispose()
        }
    }

    UIKitView(
        modifier = modifier,
        factory = { mapController.view }
    )
}
