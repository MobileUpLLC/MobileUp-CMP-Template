package ru.mobileup.template.core.map.internal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.mobileup.template.core.map.MapCameraPosition
import ru.mobileup.template.core.map.MapMarker
import ru.mobileup.template.core.theme.custom.CustomTheme

@Composable
internal actual fun PlatformMap(
    initialCameraPosition: MapCameraPosition,
    onMapReady: (MapController) -> Unit,
    onCameraPositionChange: (MapCameraPosition) -> Unit,
    onMarkerClick: (MapMarker) -> Unit,
    onClusterClick: (List<MapMarker>) -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 80.dp),
            text = "Map is not implemented for iOS",
            color = CustomTheme.colors.text.primary,
            style = CustomTheme.typography.caption.regular
        )
    }
}
