package ru.mobileup.template.core.permissions

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.RequestCanceledException
import dev.icerock.moko.permissions.camera.CAMERA
import dev.icerock.moko.permissions.contacts.READ_CONTACTS
import dev.icerock.moko.permissions.ios.PermissionsController
import dev.icerock.moko.permissions.location.BACKGROUND_LOCATION
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import dev.icerock.moko.permissions.location.LOCATION
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import dev.icerock.moko.permissions.Permission as MokoPermission

class IosPermissionService : PermissionService {

    private val permissionsController = PermissionsController()

    override suspend fun requestPermission(permission: Permission): PermissionResult {
        val mokoPermission = permission.toMokoPermission()
        val stateBefore = permissionsController.getPermissionState(mokoPermission)

        if (stateBefore == PermissionState.Granted) {
            return PermissionResult.Granted
        }

        if (stateBefore == PermissionState.DeniedAlways) {
            return PermissionResult.Denied.PermanentlyWithoutPrompt
        }

        return try {
            permissionsController.providePermission(mokoPermission)
            PermissionResult.Granted
        } catch (_: DeniedAlwaysException) {
            PermissionResult.Denied.PermanentlyByUser
        } catch (_: DeniedException) {
            PermissionResult.Denied.TemporarilyByUser
        } catch (_: RequestCanceledException) {
            PermissionResult.Denied.TemporarilyByUser
        }
    }

    override suspend fun isPermissionGranted(permission: Permission): Boolean {
        return permissionsController.isPermissionGranted(permission.toMokoPermission())
    }
}

private fun Permission.toMokoPermission(): MokoPermission = when (this) {
    Permission.Camera -> MokoPermission.CAMERA
    Permission.ReadContacts -> MokoPermission.READ_CONTACTS
    Permission.FineLocation -> MokoPermission.LOCATION
    Permission.CoarseLocation -> MokoPermission.COARSE_LOCATION
    Permission.BackgroundLocation -> MokoPermission.BACKGROUND_LOCATION
    Permission.RemoteNotification -> MokoPermission.REMOTE_NOTIFICATION
}
