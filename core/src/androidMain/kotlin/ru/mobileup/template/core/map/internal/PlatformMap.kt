package ru.mobileup.template.core.map.internal

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.yandex.mapkit.mapview.MapView
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
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentOnCameraPositionChange by rememberUpdatedState(onCameraPositionChange)
    val currentOnMarkerClick by rememberUpdatedState(onMarkerClick)
    val currentOnClusterClick by rememberUpdatedState(onClusterClick)
    val currentOnMapReady by rememberUpdatedState(onMapReady)

    val mapView = remember { MapView(context) }
    val mapController = remember(mapView) {
        AndroidMapController(
            mapView = mapView,
            onCameraPositionChange = { currentOnCameraPositionChange(it) },
            onMarkerClick = { currentOnMarkerClick(it) },
            onClusterClick = { currentOnClusterClick(it) }
        )
    }

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val density = LocalDensity.current
    val logoHorizontalPadding = with(density) { 8.dp.toPx() }.toInt()
    val logoVerticalPadding = with(density) { (statusBarPadding + 8.dp).toPx() }.toInt()

    LaunchedEffect(mapController, logoHorizontalPadding, logoVerticalPadding) {
        mapController.setLogoPadding(logoHorizontalPadding, logoVerticalPadding)
    }

    DisposableEffect(lifecycleOwner, mapController) {
        currentOnMapReady(mapController)

        val lifecycleEventObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapController.start()
                Lifecycle.Event.ON_STOP -> mapController.stop()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleEventObserver)

        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            mapController.start()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleEventObserver)
            mapController.stop()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { mapView }
    )
}
