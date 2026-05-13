package ru.mobileup.template.core.dialog.standard

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.template.core.dialog.simple.SimpleDialogControl
import ru.mobileup.template.core.dialog.simple.fakeSimpleDialogControl
import ru.mobileup.template.core.dialog.simple.simpleDialogControl

typealias StandardDialogControl = SimpleDialogControl<StandardDialogData>

fun ComponentContext.standardDialogControl(
    key: String
): StandardDialogControl {
    return simpleDialogControl(
        key = key,
        serializer = null,
        dismissableByUser = { data -> data.dismissableByUser }
    )
}

fun fakeStandardDialogControl(data: StandardDialogData = StandardDialogData.MOCK): StandardDialogControl {
    return fakeSimpleDialogControl(data)
}