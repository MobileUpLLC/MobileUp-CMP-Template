package ru.mobileup.template.features.places.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import ru.mobileup.template.core.dialog.simple.fakeSimpleDialogControl
import ru.mobileup.template.core.dialog.standard.fakeStandardDialogControl
import ru.mobileup.template.core.map.MapCommand
import ru.mobileup.template.core.map.MapMarker
import ru.mobileup.template.core.map.MapTheme
import ru.mobileup.template.core.utils.LoadableState
import ru.mobileup.template.features.places.domain.Place
import ru.mobileup.template.features.places.domain.PlaceId

class FakePlacesComponent : PlacesComponent {

    override val placesState = MutableStateFlow(LoadableState(data = Place.MOCKS))
    override val mapCommands = emptyFlow<MapCommand>()
    override val hasLocationPermission = MutableStateFlow(true)
    override val isLocationSearchInProgress = MutableStateFlow(false)
    override val theme = MutableStateFlow(MapTheme.Default)
    override val placeDialogControl = fakeSimpleDialogControl(Place.MOCKS.first())
    override val locationDialogControl = fakeStandardDialogControl()

    override fun onRefresh() = Unit
    override fun onZoomInClick() = Unit
    override fun onZoomOutClick() = Unit
    override fun onMyLocationClick() = Unit
    override fun onPlaceClick(placeId: PlaceId) = Unit
    override fun onClusterClick(markers: List<MapMarker>) = Unit
    override fun onThemeSwitch(newTheme: MapTheme) = Unit
}
