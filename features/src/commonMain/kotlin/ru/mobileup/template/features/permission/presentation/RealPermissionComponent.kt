package ru.mobileup.template.features.permission.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnResume
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.mobileup.template.core.dialog.standard.DialogButton
import ru.mobileup.template.core.dialog.standard.StandardDialogData
import ru.mobileup.template.core.dialog.standard.standardDialogControl
import ru.mobileup.template.core.error_handling.ErrorHandler
import ru.mobileup.template.core.error_handling.safeLaunch
import ru.mobileup.template.core.external_app.ExternalAppService
import ru.mobileup.template.core.generated.resources.common_cancel
import ru.mobileup.template.core.generated.resources.common_open_settings
import ru.mobileup.template.core.message.data.MessageService
import ru.mobileup.template.core.message.domain.Message
import ru.mobileup.template.core.permissions.Permission
import ru.mobileup.template.core.permissions.PermissionResult
import ru.mobileup.template.core.permissions.PermissionService
import ru.mobileup.template.core.utils.componentScope
import ru.mobileup.template.core.utils.resourceDesc
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.core.generated.resources.Res as CoreRes
import ru.mobileup.template.features.generated.resources.permission_camera_denied
import ru.mobileup.template.features.generated.resources.permission_camera_granted
import ru.mobileup.template.features.generated.resources.permission_settings_message
import ru.mobileup.template.features.generated.resources.permission_settings_title

class RealPermissionComponent(
    componentContext: ComponentContext,
    private val errorHandler: ErrorHandler,
    private val permissionService: PermissionService,
    private val messageService: MessageService,
    private val externalAppService: ExternalAppService
) : ComponentContext by componentContext, PermissionComponent {

    override val permissionState = MutableStateFlow(PermissionUiState())

    override val settingsDialogControl = standardDialogControl(key = "settingsDialog")

    init {
        lifecycle.doOnResume {
            updateCameraPermissionState()
        }
    }

    override fun onCameraPermissionClick() {
        componentScope.safeLaunch(errorHandler) {
            when (permissionService.requestPermission(Permission.Camera)) {
                PermissionResult.Granted -> showCameraGrantedMessage()
                PermissionResult.Denied.PermanentlyWithoutPrompt -> showSettingsDialog()
                PermissionResult.Denied.PermanentlyByUser,
                PermissionResult.Denied.TemporarilyByUser -> showCameraDeniedMessage()
            }

            updateCameraPermissionState()
        }
    }

    private fun updateCameraPermissionState() {
        componentScope.safeLaunch(errorHandler) {
            val isCameraPermissionGranted = permissionService.isPermissionGranted(Permission.Camera)

            permissionState.update {
                it.copy(isCameraPermissionGranted = isCameraPermissionGranted)
            }
        }
    }

    private fun showSettingsDialog() {
        settingsDialogControl.show(
            StandardDialogData(
                title = Res.string.permission_settings_title.resourceDesc(),
                message = Res.string.permission_settings_message.resourceDesc(),
                confirmButton = DialogButton(
                    text = CoreRes.string.common_open_settings.resourceDesc(),
                    action = ::openAppSettings
                ),
                cancelButton = DialogButton(
                    text = CoreRes.string.common_cancel.resourceDesc()
                )
            )
        )
    }

    private fun openAppSettings() {
        componentScope.safeLaunch(errorHandler) {
            externalAppService.openAppSettings()
        }
    }

    private fun showCameraGrantedMessage() {
        messageService.showMessage(
            Message(text = Res.string.permission_camera_granted.resourceDesc())
        )
    }

    private fun showCameraDeniedMessage() {
        messageService.showMessage(
            Message(text = Res.string.permission_camera_denied.resourceDesc())
        )
    }
}
