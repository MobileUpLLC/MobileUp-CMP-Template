package ru.mobileup.template.features.pokemons.presentation.dialogs_demo

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.template.core.dialog.simple.simpleDialogControl
import ru.mobileup.template.core.dialog.standard.DialogButton
import ru.mobileup.template.core.dialog.standard.StandardDialogData
import ru.mobileup.template.core.dialog.standard.standardDialogControl
import ru.mobileup.template.core.message.data.MessageService
import ru.mobileup.template.core.message.domain.Message
import ru.mobileup.template.core.utils.resourceDesc
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_close
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_custom_dialog_message
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_custom_dialog_title
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_dismissible_sheet_message
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_dismissible_sheet_title
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_got_it
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_message_action
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_message_sheet_message
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_message_sheet_title
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_message_text
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_non_dismissible_sheet_message
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_non_dismissible_sheet_title
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_show_message
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_standard_dialog_cancel
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_standard_dialog_confirm
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_standard_dialog_message
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_standard_dialog_title

class RealDialogsDemoComponent(
    componentContext: ComponentContext,
    private val onOutput: (DialogsDemoComponent.Output) -> Unit,
    private val messageService: MessageService
) : ComponentContext by componentContext, DialogsDemoComponent {

    override val standardDialogControl = standardDialogControl(key = "standardDialog")

    override val customDialogControl = simpleDialogControl<DialogsDemoDialogData>(
        key = "customDialog"
    )

    override val bottomSheetControl = simpleDialogControl<DialogsDemoBottomSheetData>(
        key = "bottomSheet",
        dismissableByUser = { it.dismissableByUser }
    )

    override fun onBackClick() {
        onOutput(DialogsDemoComponent.Output.BackRequested)
    }

    override fun onStandardDialogClick() {
        standardDialogControl.show(
            StandardDialogData(
                title = Res.string.pokemons_dialogs_demo_standard_dialog_title.resourceDesc(),
                message = Res.string.pokemons_dialogs_demo_standard_dialog_message.resourceDesc(),
                confirmButton = DialogButton(
                    text = Res.string.pokemons_dialogs_demo_standard_dialog_confirm.resourceDesc()
                ),
                cancelButton = DialogButton(
                    text = Res.string.pokemons_dialogs_demo_standard_dialog_cancel.resourceDesc()
                )
            )
        )
    }

    override fun onCustomDialogClick() {
        customDialogControl.show(
            DialogsDemoDialogData(
                title = Res.string.pokemons_dialogs_demo_custom_dialog_title,
                message = Res.string.pokemons_dialogs_demo_custom_dialog_message,
                buttonText = Res.string.pokemons_dialogs_demo_got_it
            )
        )
    }

    override fun onDismissibleBottomSheetClick() {
        bottomSheetControl.show(
            DialogsDemoBottomSheetData(
                title = Res.string.pokemons_dialogs_demo_dismissible_sheet_title,
                message = Res.string.pokemons_dialogs_demo_dismissible_sheet_message,
                cancelButtonText = Res.string.pokemons_dialogs_demo_close,
                dismissableByUser = true
            )
        )
    }

    override fun onNonDismissibleBottomSheetClick() {
        bottomSheetControl.show(
            DialogsDemoBottomSheetData(
                title = Res.string.pokemons_dialogs_demo_non_dismissible_sheet_title,
                message = Res.string.pokemons_dialogs_demo_non_dismissible_sheet_message,
                cancelButtonText = Res.string.pokemons_dialogs_demo_close,
                dismissableByUser = false
            )
        )
    }

    override fun onMessageBottomSheetClick() {
        bottomSheetControl.show(
            DialogsDemoBottomSheetData(
                title = Res.string.pokemons_dialogs_demo_message_sheet_title,
                message = Res.string.pokemons_dialogs_demo_message_sheet_message,
                cancelButtonText = Res.string.pokemons_dialogs_demo_close,
                dismissableByUser = true,
                showMessageButton = true,
                messageButtonText = Res.string.pokemons_dialogs_demo_show_message
            )
        )
    }

    override fun onCustomDialogCancelClick() {
        customDialogControl.dismiss()
    }

    override fun onBottomSheetCancelClick() {
        bottomSheetControl.dismiss()
    }

    override fun onShowMessageOverSheetClick() {
        messageService.showMessage(
            Message(
                text = Res.string.pokemons_dialogs_demo_message_text.resourceDesc(),
                actionTitle = Res.string.pokemons_dialogs_demo_message_action.resourceDesc()
            )
        )
    }
}
