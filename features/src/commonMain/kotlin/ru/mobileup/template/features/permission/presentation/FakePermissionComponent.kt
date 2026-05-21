package ru.mobileup.template.features.permission.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.template.core.dialog.standard.fakeStandardDialogControl

class FakePermissionComponent : PermissionComponent {

    override val permissionState = MutableStateFlow(PermissionUiState())

    override val settingsDialogControl = fakeStandardDialogControl()

    override fun onCameraPermissionClick() = Unit
}
