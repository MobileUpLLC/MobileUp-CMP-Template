package ru.mobileup.template.core.dialog.standard

import ru.mobileup.template.core.utils.StringDesc
import ru.mobileup.template.core.utils.desc

data class StandardDialogData(
    val title: StringDesc,
    val message: StringDesc? = null,
    val confirmButton: DialogButton,
    val cancelButton: DialogButton? = null,
    val dismissableByUser: Boolean = true
) {

    companion object {
        val MOCK = StandardDialogData(
            title = "Title".desc(),
            message = "Message".desc(),
            confirmButton = DialogButton(text = "Next".desc()),
            cancelButton = DialogButton(text = "Cancel".desc())
        )
    }
}

data class DialogButton(
    val text: StringDesc,
    val style: DialogButtonStyle = DialogButtonStyle.Normal,
    val action: (() -> Unit)? = null
)

enum class DialogButtonStyle {
    Normal, Destructive
}