package ru.mobileup.template.features.places.presentation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.template.core.dialog.simple.SimpleDialogControl
import ru.mobileup.template.core.dialog.standard.StandardDialogControl
import ru.mobileup.template.core.map.MapCommand
import ru.mobileup.template.core.map.MapMarker
import ru.mobileup.template.core.map.MapTheme
import ru.mobileup.template.core.utils.LoadableState
import ru.mobileup.template.features.places.domain.Place
import ru.mobileup.template.features.places.domain.PlaceId

interface PlacesComponent {

    val placesState: StateFlow<LoadableState<List<Place>>>

    val mapCommands: Flow<MapCommand>

    val hasLocationPermission: StateFlow<Boolean>

    val isLocationSearchInProgress: StateFlow<Boolean>

    val theme: StateFlow<MapTheme>

    val placeDialogControl: SimpleDialogControl<Place>

    val locationDialogControl: StandardDialogControl

    fun onRefresh()

    fun onZoomInClick()

    fun onZoomOutClick()

    fun onMyLocationClick()

    fun onPlaceClick(placeId: PlaceId)

    fun onClusterClick(markers: List<MapMarker>)

    fun onThemeSwitch(newTheme: MapTheme)
}
