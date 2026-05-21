package ru.mobileup.template.features.dialogs.presentation

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.template.core.dialog.simple.simpleDialogControl
import ru.mobileup.template.core.dialog.standard.DialogButton
import ru.mobileup.template.core.dialog.standard.StandardDialogData
import ru.mobileup.template.core.dialog.standard.standardDialogControl
import ru.mobileup.template.core.message.data.MessageService
import ru.mobileup.template.core.message.domain.Message
import ru.mobileup.template.core.utils.resourceDesc
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.dialogs_close
import ru.mobileup.template.features.generated.resources.dialogs_custom_dialog_message
import ru.mobileup.template.features.generated.resources.dialogs_custom_dialog_title
import ru.mobileup.template.features.generated.resources.dialogs_dismissible_sheet_message
import ru.mobileup.template.features.generated.resources.dialogs_dismissible_sheet_title
import ru.mobileup.template.features.generated.resources.dialogs_got_it
import ru.mobileup.template.features.generated.resources.dialogs_message_action
import ru.mobileup.template.features.generated.resources.dialogs_message_sheet_message
import ru.mobileup.template.features.generated.resources.dialogs_message_sheet_title
import ru.mobileup.template.features.generated.resources.dialogs_message_text
import ru.mobileup.template.features.generated.resources.dialogs_non_dismissible_sheet_message
import ru.mobileup.template.features.generated.resources.dialogs_non_dismissible_sheet_title
import ru.mobileup.template.features.generated.resources.dialogs_show_message
import ru.mobileup.template.features.generated.resources.dialogs_standard_dialog_cancel
import ru.mobileup.template.features.generated.resources.dialogs_standard_dialog_confirm
import ru.mobileup.template.features.generated.resources.dialogs_standard_dialog_message
import ru.mobileup.template.features.generated.resources.dialogs_standard_dialog_title

class RealDialogsComponent(
    componentContext: ComponentContext,
    private val messageService: MessageService
) : ComponentContext by componentContext, DialogsComponent {

    override val standardDialogControl = standardDialogControl(key = "standardDialog")

    override val customDialogControl = simpleDialogControl<DialogsDialogData>(
        key = "customDialog"
    )

    override val bottomSheetControl = simpleDialogControl<DialogsBottomSheetData>(
        key = "bottomSheet",
        dismissableByUser = { it.dismissableByUser }
    )

    override fun onStandardDialogClick() {
        standardDialogControl.show(
            StandardDialogData(
                title = Res.string.dialogs_standard_dialog_title.resourceDesc(),
                message = Res.string.dialogs_standard_dialog_message.resourceDesc(),
                confirmButton = DialogButton(
                    text = Res.string.dialogs_standard_dialog_confirm.resourceDesc()
                ),
                cancelButton = DialogButton(
                    text = Res.string.dialogs_standard_dialog_cancel.resourceDesc()
                )
            )
        )
    }

    override fun onCustomDialogClick() {
        customDialogControl.show(
            DialogsDialogData(
                title = Res.string.dialogs_custom_dialog_title,
                message = Res.string.dialogs_custom_dialog_message,
                buttonText = Res.string.dialogs_got_it
            )
        )
    }

    override fun onDismissibleBottomSheetClick() {
        bottomSheetControl.show(
            DialogsBottomSheetData(
                title = Res.string.dialogs_dismissible_sheet_title,
                message = Res.string.dialogs_dismissible_sheet_message,
                cancelButtonText = Res.string.dialogs_close,
                dismissableByUser = true
            )
        )
    }

    override fun onNonDismissibleBottomSheetClick() {
        bottomSheetControl.show(
            DialogsBottomSheetData(
                title = Res.string.dialogs_non_dismissible_sheet_title,
                message = Res.string.dialogs_non_dismissible_sheet_message,
                cancelButtonText = Res.string.dialogs_close,
                dismissableByUser = false
            )
        )
    }

    override fun onMessageBottomSheetClick() {
        bottomSheetControl.show(
            DialogsBottomSheetData(
                title = Res.string.dialogs_message_sheet_title,
                message = Res.string.dialogs_message_sheet_message,
                cancelButtonText = Res.string.dialogs_close,
                dismissableByUser = true,
                showMessageButton = true,
                messageButtonText = Res.string.dialogs_show_message
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
                text = Res.string.dialogs_message_text.resourceDesc(),
                actionTitle = Res.string.dialogs_message_action.resourceDesc()
            )
        )
    }
}
