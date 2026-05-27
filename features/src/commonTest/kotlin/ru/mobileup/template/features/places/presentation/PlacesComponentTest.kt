package ru.mobileup.template.features.places.presentation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.mobileup.template.core.location.GeoCoordinate
import ru.mobileup.template.core.location.LocationError
import ru.mobileup.template.core.map.MapCameraPosition
import ru.mobileup.template.core.map.MapCommand
import ru.mobileup.template.core.map.MapMarker
import ru.mobileup.template.core.map.MapTheme
import ru.mobileup.template.core.permissions.PermissionResult
import ru.mobileup.template.core.utils.activeChild
import ru.mobileup.template.core_testing.test_services.TestExternalAppService
import ru.mobileup.template.features.places.createPlacesComponent
import ru.mobileup.template.features.places.domain.Place
import ru.mobileup.template.features.places.domain.filterInMoscowRegion
import ru.mobileup.template.features.utils.componentTest

class PlacesComponentTest : FunSpec({

    componentTest("loads places and focuses Moscow region initially") {
        // 🛠️ Prepare a places screen with map command collection
        val mapCommands = mutableListOf<MapCommand>()
        val component = setupComponent {
            createPlacesComponent(it).also { component ->
                collectFlow(component.mapCommands, mapCommands)
            }
        }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify the loaded places are shown
        component.placesState.value.loading shouldBe false
        component.placesState.value.error shouldBe null
        component.placesState.value.data shouldBe Place.MOCKS

        // ✅ Verify the map focus command for Moscow-region places is emitted
        mapCommands shouldBe listOf(
            MapCommand.MoveToBoundingBox(
                coordinates = Place.MOCKS.filterInMoscowRegion().map { it.coordinate }
            )
        )
    }

    componentTest("emits zoom commands from map controls") {
        // 🛠️ Prepare a loaded places screen with map command collection
        val mapCommands = mutableListOf<MapCommand>()
        val component = setupComponent {
            createPlacesComponent(it).also { component ->
                collectFlow(component.mapCommands, mapCommands)
            }
        }
        advanceUntilIdle()

        // ▶️ Clear initial map commands
        mapCommands.clear()

        // ▶️ Use zoom controls
        component.onZoomInClick()
        runCurrent()
        component.onZoomOutClick()
        runCurrent()

        // ✅ Verify zoom-in and zoom-out commands are emitted in order
        mapCommands shouldBe listOf(MapCommand.ZoomIn, MapCommand.ZoomOut)
    }

    componentTest("moves map to selected cluster bounds") {
        // 🛠️ Prepare a loaded places screen with map command collection and marker cluster
        val mapCommands = mutableListOf<MapCommand>()
        val component = setupComponent {
            createPlacesComponent(it).also { component ->
                collectFlow(component.mapCommands, mapCommands)
            }
        }
        advanceUntilIdle()
        val markers = listOf(
            MapMarker("1", GeoCoordinate(55.741004, 37.587734)),
            MapMarker("2", GeoCoordinate(55.772004, 37.617734))
        )

        // ▶️ Clear initial map commands
        mapCommands.clear()

        // ▶️ Select the marker cluster
        component.onClusterClick(markers)
        runCurrent()

        // ✅ Verify the map focus command for cluster coordinates is emitted
        mapCommands shouldBe listOf(
            MapCommand.MoveToBoundingBox(
                coordinates = markers.map { it.coordinate }
            )
        )
    }

    componentTest("shows selected place dialog") {
        // 🛠️ Prepare a loaded places screen
        val component = setupComponent { createPlacesComponent(it) }
        advanceUntilIdle()
        val selectedPlace = Place.MOCKS.first()

        // ▶️ Select a place from the map
        component.onPlaceClick(selectedPlace.id)

        // ✅ Verify the place dialog is shown with the selected place
        component.placeDialogControl.activeChild shouldBe selectedPlace
    }

    componentTest("changes map theme") {
        // 🛠️ Prepare a places screen with the default map theme
        val component = setupComponent { createPlacesComponent(it) }
        component.theme.value shouldBe MapTheme.Default

        // ▶️ Switch to another map theme
        component.onThemeSwitch(MapTheme.Dark)

        // ✅ Verify the selected map theme is exposed by the component
        component.theme.value shouldBe MapTheme.Dark
    }

    componentTest("shows current location marker when permission is already granted") {
        // 🛠️ Prepare granted location permission
        permissionService.result = PermissionResult.Granted

        // ▶️ Open the places screen
        val component = setupComponent { createPlacesComponent(it) }

        // ✅ Verify location permission state is enabled
        component.hasLocationPermission.value shouldBe true
    }

    componentTest("moves map to current location when permission is granted") {
        // 🛠️ Prepare granted location permission, successful current location, and map command collection
        permissionService.result = PermissionResult.Granted
        val coordinate = GeoCoordinate(56.839104, 60.60825)
        locationService.setSuccessLocationResult(coordinate)
        val mapCommands = mutableListOf<MapCommand>()
        val component = setupComponent {
            createPlacesComponent(it).also { component ->
                collectFlow(component.mapCommands, mapCommands)
            }
        }

        // ▶️ Wait for initial screen stabilization and clear initial map commands
        advanceUntilIdle()
        mapCommands.clear()

        // ▶️ Request current location
        component.onMyLocationClick()
        advanceUntilIdle()

        // ✅ Verify location permission remains enabled
        component.hasLocationPermission.value shouldBe true

        // ✅ Verify the map move command for current location is emitted
        mapCommands shouldBe listOf(
            MapCommand.MoveTo(
                coordinate = coordinate,
                zoom = MapCameraPosition.CLOSE_MAP_ZOOM,
                dontZoomOut = true
            )
        )
    }

    componentTest("shows app settings dialog when location permission is permanently denied") {
        // 🛠️ Prepare permanently denied location permission
        permissionService.result = PermissionResult.Denied.PermanentlyWithoutPrompt
        val component = setupComponent { createPlacesComponent(it) }

        // ▶️ Request current location
        component.onMyLocationClick()
        advanceUntilIdle()

        // ✅ Verify location permission is disabled
        component.hasLocationPermission.value shouldBe false

        // ✅ Verify the permission settings dialog is shown
        val dialog = component.locationDialogControl.activeChild.shouldNotBeNull()

        // ▶️ Confirm the dialog action
        dialog.confirmButton.action.shouldNotBeNull().invoke()

        // ✅ Verify app settings are requested
        externalAppService.lastInteraction shouldBe TestExternalAppService.Interaction.OpenAppSettings
    }

    componentTest("ignores temporary location permission denial without dialog") {
        // 🛠️ Prepare temporarily denied location permission
        permissionService.result = PermissionResult.Denied.TemporarilyByUser
        val component = setupComponent { createPlacesComponent(it) }

        // ▶️ Request current location
        component.onMyLocationClick()
        advanceUntilIdle()

        // ✅ Verify location permission is disabled
        component.hasLocationPermission.value shouldBe false

        // ✅ Verify no settings dialog or external-app action is shown
        component.locationDialogControl.activeChild shouldBe null
        externalAppService.wasNoInteractions shouldBe true
    }

    componentTest("shows location settings dialog when location services are disabled") {
        // 🛠️ Prepare granted location permission and disabled location services
        permissionService.result = PermissionResult.Granted
        locationService.setFailureLocationResult(LocationError.LocationServicesDisabled)
        val component = setupComponent { createPlacesComponent(it) }

        // ▶️ Request current location
        component.onMyLocationClick()
        advanceUntilIdle()

        // ✅ Verify the location-services dialog is shown
        val dialog = component.locationDialogControl.activeChild.shouldNotBeNull()

        // ▶️ Confirm the dialog action
        dialog.confirmButton.action.shouldNotBeNull().invoke()

        // ✅ Verify location settings are requested
        externalAppService.lastInteraction shouldBe TestExternalAppService.Interaction.OpenLocationSettings
    }

    componentTest("shows location error message when current location cannot be determined") {
        // 🛠️ Prepare granted location permission and an unavailable current-location result
        permissionService.result = PermissionResult.Granted
        locationService.setFailureLocationResult(LocationError.CannotDetermineLocation)
        val component = setupComponent { createPlacesComponent(it) }

        // ▶️ Request current location
        component.onMyLocationClick()
        advanceUntilIdle()

        // ✅ Verify an error message is shown
        messageService.lastMessage.shouldNotBeNull()

        // ✅ Verify no location settings dialog is shown
        component.locationDialogControl.activeChild shouldBe null
    }
})
