package ru.mobileup.template.features.places.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.mobileup.template.core.dialog.BottomSheet
import ru.mobileup.template.core.dialog.standard.StandardDialog
import ru.mobileup.template.core.map.Map
import ru.mobileup.template.core.map.MapMarker
import ru.mobileup.template.core.map.MapTheme
import ru.mobileup.template.core.message.presentation.noOverlapByMessage
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.theme.custom.CustomTheme
import ru.mobileup.template.core.utils.SystemBarIconsColor
import ru.mobileup.template.core.utils.SystemBars
import ru.mobileup.template.core.widget.FullscreenCircularProgress
import ru.mobileup.template.core.widget.LceWidget
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.places_lat
import ru.mobileup.template.features.generated.resources.places_lng
import ru.mobileup.template.features.places.domain.Place
import ru.mobileup.template.features.places.domain.PlaceId
import ru.mobileup.template.features.places.presentation.widget.MapMyLocationButton
import ru.mobileup.template.features.places.presentation.widget.MapThemeSwitch
import ru.mobileup.template.features.places.presentation.widget.MapZoomButtons

@Composable
fun PlacesUi(
    component: PlacesComponent,
    modifier: Modifier = Modifier
) {
    ThemedSystemBars(component)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CustomTheme.colors.background.screen,
    ) { innerPadding ->
        PlacesContent(component, innerPadding = innerPadding)
    }

    BottomSheet(component.placeDialogControl) { place ->
        PlaceInfo(place)
    }

    StandardDialog(dialogControl = component.locationDialogControl)
}

@Composable
private fun ThemedSystemBars(component: PlacesComponent) {
    val theme by component.theme.collectAsState()
    val iconsColor = when (theme) {
        MapTheme.Bright, MapTheme.Default -> SystemBarIconsColor.Dark
        MapTheme.Dark -> SystemBarIconsColor.Light
    }

    SystemBars(
        statusBarIconsColor = iconsColor,
        navigationBarIconsColor = iconsColor,
        navigationBarColor = Color.Transparent
    )
}

@Composable
private fun PlacesContent(
    component: PlacesComponent,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val theme by component.theme.collectAsState()
    val hasLocationPermission by component.hasLocationPermission.collectAsState()

    val placesState by component.placesState.collectAsState()
    val places = placesState.data.orEmpty()
    val markers = remember(places) {
        places.map { place ->
            MapMarker(id = place.id.value, coordinate = place.coordinate)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Map(
            modifier = Modifier.fillMaxSize(),
            mapCommands = component.mapCommands,
            theme = theme,
            onMarkerClick = { component.onPlaceClick(PlaceId(it.id)) },
            onClusterClick = component::onClusterClick,
            showCurrentLocationMarker = hasLocationPermission,
            markers = markers
        )

        LceWidget(
            modifier = Modifier.fillMaxSize(),
            state = placesState,
            loadingContent = {
                FullscreenCircularProgress(overlay = true)
            },
            innerPadding = innerPadding,
            onRetryClick = component::onRefresh
        ) { _, contentPadding ->
            MapControls(component, Modifier.padding(contentPadding))
        }
    }
}

@Composable
private fun MapControls(
    component: PlacesComponent,
    modifier: Modifier = Modifier
) {
    val isLocationSearchInProgress by component.isLocationSearchInProgress.collectAsState()
    val theme by component.theme.collectAsState()

    Box(
        modifier.fillMaxSize()
    ) {
        MapThemeSwitch(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp),
            theme = theme,
            onThemeSwitch = component::onThemeSwitch
        )

        MapZoomButtons(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            theme = theme,
            onZoomInClick = component::onZoomInClick,
            onZoomOutClick = component::onZoomOutClick
        )

        MapMyLocationButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp, end = 8.dp)
                .noOverlapByMessage(),
            theme = theme,
            onClick = component::onMyLocationClick,
            isLocationSearchInProgress = isLocationSearchInProgress
        )
    }
}

@Composable
private fun PlaceInfo(
    place: Place,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = place.name,
            color = CustomTheme.colors.text.primary,
            style = CustomTheme.typography.body.regular
        )
        Text(
            text = stringResource(Res.string.places_lat, place.coordinate.latitude.toString()),
            color = CustomTheme.colors.text.primary,
            style = CustomTheme.typography.body.regular
        )
        Text(
            text = stringResource(Res.string.places_lng, place.coordinate.longitude.toString()),
            color = CustomTheme.colors.text.primary,
            style = CustomTheme.typography.body.regular
        )
    }
}

@Preview
@Composable
private fun PlacesUiPreview() {
    AppTheme {
        PlacesUi(FakePlacesComponent())
    }
}
