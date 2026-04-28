package ru.mobileup.template.features.pokemons.presentation.dialogs_demo

import org.jetbrains.compose.resources.StringResource
import ru.mobileup.template.core.dialog.simple.SimpleDialogControl
import ru.mobileup.template.core.dialog.standard.StandardDialogControl

interface DialogsDemoComponent {

    val standardDialogControl: StandardDialogControl

    val customDialogControl: SimpleDialogControl<DialogsDemoDialogData>

    val bottomSheetControl: SimpleDialogControl<DialogsDemoBottomSheetData>

    fun onBackClick()

    fun onStandardDialogClick()

    fun onCustomDialogClick()

    fun onDismissibleBottomSheetClick()

    fun onNonDismissibleBottomSheetClick()

    fun onMessageBottomSheetClick()

    fun onCustomDialogCancelClick()

    fun onBottomSheetCancelClick()

    fun onShowMessageOverSheetClick()

    sealed interface Output {
        data object BackRequested : Output
    }
}

data class DialogsDemoDialogData(
    val title: StringResource,
    val message: StringResource,
    val buttonText: StringResource
)

data class DialogsDemoBottomSheetData(
    val title: StringResource,
    val message: StringResource,
    val cancelButtonText: StringResource,
    val dismissableByUser: Boolean,
    val showMessageButton: Boolean = false,
    val messageButtonText: StringResource? = null
)
