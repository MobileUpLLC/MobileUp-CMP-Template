package ru.mobileup.template.core.permissions

import android.content.Context
import android.os.SystemClock
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import dev.icerock.moko.permissions.camera.CAMERA
import dev.icerock.moko.permissions.contacts.READ_CONTACTS
import dev.icerock.moko.permissions.location.BACKGROUND_LOCATION
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import dev.icerock.moko.permissions.location.LOCATION
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import ru.mobileup.template.core.activity.ActivityProvider
import dev.icerock.moko.permissions.Permission as MokoPermission

class AndroidPermissionService(
    applicationContext: Context,
    activityProvider: ActivityProvider,
    applicationScope: CoroutineScope
) : PermissionService {

    private companion object {
        private const val AUTO_DENIED_THRESHOLD_MS = 500L
    }

    private val permissionsController = PermissionsController(applicationContext)

    init {
        applicationScope.launch {
            activityProvider.activityStateFlow
                .filterNotNull()
                .distinctUntilChanged()
                .collect(permissionsController::bind)
        }
    }

    override suspend fun requestPermission(permission: Permission): PermissionResult {
        val startedAt = SystemClock.elapsedRealtime()
        return try {
            permissionsController.providePermission(permission.toMokoPermission())
            PermissionResult.Granted
        } catch (_: DeniedAlwaysException) {
            val elapsedMs = SystemClock.elapsedRealtime() - startedAt
            if (elapsedMs < AUTO_DENIED_THRESHOLD_MS) {
                PermissionResult.Denied.PermanentlyWithoutPrompt
            } else {
                PermissionResult.Denied.PermanentlyByUser
            }
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
