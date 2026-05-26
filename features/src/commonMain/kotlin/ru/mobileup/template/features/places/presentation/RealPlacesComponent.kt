package ru.mobileup.template.features.places.presentation

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import ru.mobileup.template.core.dialog.simple.simpleDialogControl
import ru.mobileup.template.core.dialog.standard.DialogButton
import ru.mobileup.template.core.dialog.standard.StandardDialogData
import ru.mobileup.template.core.dialog.standard.standardDialogControl
import ru.mobileup.template.core.error_handling.ErrorHandler
import ru.mobileup.template.core.error_handling.safeLaunch
import ru.mobileup.template.core.external_app.ExternalAppService
import ru.mobileup.template.core.generated.resources.common_cancel
import ru.mobileup.template.core.generated.resources.common_open_settings
import ru.mobileup.template.core.location.LocationError
import ru.mobileup.template.core.location.LocationResult
import ru.mobileup.template.core.location.LocationService
import ru.mobileup.template.core.map.MapCameraPosition
import ru.mobileup.template.core.map.MapCommand
import ru.mobileup.template.core.map.MapMarker
import ru.mobileup.template.core.map.MapTheme
import ru.mobileup.template.core.message.data.MessageService
import ru.mobileup.template.core.message.domain.Message
import ru.mobileup.template.core.permissions.Permission
import ru.mobileup.template.core.permissions.PermissionResult
import ru.mobileup.template.core.permissions.PermissionService
import ru.mobileup.template.core.utils.componentScope
import ru.mobileup.template.core.utils.observe
import ru.mobileup.template.core.utils.resourceDesc
import ru.mobileup.template.core.utils.withProgress
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.places_cannot_determine_location
import ru.mobileup.template.features.generated.resources.places_location_disabled_confirm
import ru.mobileup.template.features.generated.resources.places_location_disabled_message
import ru.mobileup.template.features.generated.resources.places_location_disabled_title
import ru.mobileup.template.features.generated.resources.places_permission_denied_message
import ru.mobileup.template.features.generated.resources.places_permission_denied_title
import ru.mobileup.template.features.places.data.PlacesRepository
import ru.mobileup.template.features.places.domain.Place
import ru.mobileup.template.features.places.domain.PlaceId
import ru.mobileup.template.features.places.domain.filterInMoscowRegion
import ru.mobileup.template.core.generated.resources.Res as CoreRes

class RealPlacesComponent(
    componentContext: ComponentContext,
    private val permissionService: PermissionService,
    private val locationService: LocationService,
    private val messageService: MessageService,
    private val errorHandler: ErrorHandler,
    placesRepository: PlacesRepository,
    private val externalAppService: ExternalAppService
) : ComponentContext by componentContext, PlacesComponent {

    private val placesReplica = placesRepository.placesReplica
    override val placesState = placesReplica.observe(this, errorHandler)

    override val mapCommands = MutableSharedFlow<MapCommand>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val hasLocationPermission = MutableStateFlow(false)

    override val isLocationSearchInProgress = MutableStateFlow(false)

    override val theme = MutableStateFlow(MapTheme.Default)

    override val placeDialogControl = simpleDialogControl(
        key = "placeDialogControl",
        serializer = Place.serializer()
    )

    override val locationDialogControl = standardDialogControl(
        key = "locationDialogControl"
    )

    init {
        componentScope.safeLaunch(errorHandler) {
            hasLocationPermission.value =
                permissionService.isPermissionGranted(Permission.FineLocation)
        }

        componentScope.safeLaunch(errorHandler) {
            placesState.firstOrNull { !it.data.isNullOrEmpty() }?.let { state ->
                val moscowPlaces = state.data.orEmpty().filterInMoscowRegion()
                if (moscowPlaces.isNotEmpty()) {
                    mapCommands.emit(
                        MapCommand.MoveToBoundingBox(
                            coordinates = moscowPlaces.map { it.coordinate }
                        )
                    )
                }
            }
        }
    }

    override fun onRefresh() {
        placesReplica.refresh()
    }

    override fun onZoomInClick() {
        mapCommands.tryEmit(MapCommand.ZoomIn)
    }

    override fun onZoomOutClick() {
        mapCommands.tryEmit(MapCommand.ZoomOut)
    }

    override fun onMyLocationClick() {
        componentScope.safeLaunch(errorHandler) {
            when (val result = permissionService.requestPermission(Permission.FineLocation)) {
                PermissionResult.Granted -> {
                    hasLocationPermission.value = true
                    requestCurrentLocation()
                }

                is PermissionResult.Denied -> {
                    hasLocationPermission.value = false
                    if (result is PermissionResult.Denied.PermanentlyWithoutPrompt) {
                        showLocationPermissionPermanentlyDeniedDialog()
                    }
                }
            }
        }
    }

    override fun onPlaceClick(placeId: PlaceId) {
        val place = placesState.value.data?.firstOrNull { it.id == placeId } ?: return
        placeDialogControl.show(place)
    }

    override fun onClusterClick(markers: List<MapMarker>) {
        mapCommands.tryEmit(
            MapCommand.MoveToBoundingBox(
                coordinates = markers.map { it.coordinate }
            )
        )
    }

    override fun onThemeSwitch(newTheme: MapTheme) {
        theme.value = newTheme
    }

    private suspend fun requestCurrentLocation() {
        val result = withProgress(isLocationSearchInProgress) {
            locationService.getCurrentLocation()
        }

        when (result) {
            is LocationResult.Success -> {
                mapCommands.emit(
                    MapCommand.MoveTo(
                        coordinate = result.location.coordinate,
                        zoom = MapCameraPosition.CLOSE_MAP_ZOOM,
                        dontZoomOut = true
                    )
                )
            }

            is LocationResult.Failure -> {
                if (result.error is LocationError.LocationServicesDisabled) {
                    showLocationServiceDisabledDialog()
                } else {
                    messageService.showMessage(
                        Message(Res.string.places_cannot_determine_location.resourceDesc())
                    )
                }
            }
        }
    }

    private fun showLocationPermissionPermanentlyDeniedDialog() {
        locationDialogControl.show(
            StandardDialogData(
                title = Res.string.places_permission_denied_title.resourceDesc(),
                message = Res.string.places_permission_denied_message.resourceDesc(),
                confirmButton = DialogButton(
                    text = CoreRes.string.common_open_settings.resourceDesc(),
                    action = {
                        componentScope.safeLaunch(errorHandler) {
                            externalAppService.openAppSettings()
                        }
                    }
                ),
                cancelButton = DialogButton(
                    text = CoreRes.string.common_cancel.resourceDesc()
                )
            )
        )
    }

    private fun showLocationServiceDisabledDialog() {
        locationDialogControl.show(
            StandardDialogData(
                title = Res.string.places_location_disabled_title.resourceDesc(),
                message = Res.string.places_location_disabled_message.resourceDesc(),
                confirmButton = DialogButton(
                    text = Res.string.places_location_disabled_confirm.resourceDesc(),
                    action = {
                        componentScope.safeLaunch(errorHandler) {
                            externalAppService.openLocationSettings()
                        }
                    }
                ),
                cancelButton = DialogButton(
                    text = CoreRes.string.common_cancel.resourceDesc()
                )
            )
        )
    }
}
