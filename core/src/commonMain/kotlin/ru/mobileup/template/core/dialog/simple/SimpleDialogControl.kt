package ru.mobileup.template.core.dialog.simple

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import ru.mobileup.template.core.dialog.DialogControl
import ru.mobileup.template.core.dialog.dialogControl
import ru.mobileup.template.core.dialog.fakeDialogControl

typealias SimpleDialogControl<T> = DialogControl<T, T>

fun <T : Any> ComponentContext.simpleDialogControl(
    key: String,
    serializer: KSerializer<T>? = null,
    dismissableByUser: (T) -> Boolean = { true },
): SimpleDialogControl<T> {
    return dialogControl(
        key = key,
        dialogComponentFactory = { data, _, _ -> data },
        serializer = serializer,
        dismissableByUser = { data, _ -> MutableStateFlow(dismissableByUser(data)) }
    )
}

fun <T : Any> fakeSimpleDialogControl(data: T): SimpleDialogControl<T> {
    return fakeDialogControl(data, data)
}