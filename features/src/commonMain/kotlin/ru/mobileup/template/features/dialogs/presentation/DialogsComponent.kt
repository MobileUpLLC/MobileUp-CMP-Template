package ru.mobileup.template.features.dialogs.presentation

import org.jetbrains.compose.resources.StringResource
import ru.mobileup.template.core.dialog.simple.SimpleDialogControl
import ru.mobileup.template.core.dialog.standard.StandardDialogControl

interface DialogsComponent {

    val standardDialogControl: StandardDialogControl

    val customDialogControl: SimpleDialogControl<DialogsDialogData>

    val bottomSheetControl: SimpleDialogControl<DialogsBottomSheetData>

    fun onStandardDialogClick()

    fun onCustomDialogClick()

    fun onDismissibleBottomSheetClick()

    fun onNonDismissibleBottomSheetClick()

    fun onMessageBottomSheetClick()

    fun onCustomDialogCancelClick()

    fun onBottomSheetCancelClick()

    fun onShowMessageOverSheetClick()
}

data class DialogsDialogData(
    val title: StringResource,
    val message: StringResource,
    val buttonText: StringResource
)

data class DialogsBottomSheetData(
    val title: StringResource,
    val message: StringResource,
    val cancelButtonText: StringResource,
    val dismissableByUser: Boolean,
    val showMessageButton: Boolean = false,
    val messageButtonText: StringResource? = null
)
