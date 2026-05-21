package ru.mobileup.template.features.permission.presentation

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.template.core.dialog.standard.StandardDialogControl

interface PermissionComponent {

    val permissionState: StateFlow<PermissionUiState>

    val settingsDialogControl: StandardDialogControl

    fun onCameraPermissionClick()
}

data class PermissionUiState(
    val isCameraPermissionGranted: Boolean = false
)
