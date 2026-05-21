package ru.mobileup.template.features.dialogs.presentation

import com.arkivanov.decompose.router.slot.ChildSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.template.core.dialog.DialogControl
import ru.mobileup.template.core.dialog.standard.StandardDialogData

class FakeDialogsComponent : DialogsComponent {

    override val standardDialogControl = emptyDialogControl<StandardDialogData, StandardDialogData>()

    override val customDialogControl = emptyDialogControl<DialogsDialogData, DialogsDialogData>()

    override val bottomSheetControl =
        emptyDialogControl<DialogsBottomSheetData, DialogsBottomSheetData>()

    override fun onStandardDialogClick() = Unit

    override fun onCustomDialogClick() = Unit

    override fun onDismissibleBottomSheetClick() = Unit

    override fun onNonDismissibleBottomSheetClick() = Unit

    override fun onMessageBottomSheetClick() = Unit

    override fun onCustomDialogCancelClick() = Unit

    override fun onBottomSheetCancelClick() = Unit

    override fun onShowMessageOverSheetClick() = Unit
}

private fun <C : Any, T : Any> emptyDialogControl(): DialogControl<C, T> {
    return object : DialogControl<C, T> {
        override val dialogSlot: StateFlow<ChildSlot<*, T>> = MutableStateFlow(ChildSlot<C, T>(null))
        override val dismissableByUser: StateFlow<Boolean> = MutableStateFlow(true)
        override val dismissedEvent: Flow<Unit> = MutableSharedFlow()

        override fun show(config: C) = Unit

        override fun dismiss() = Unit
    }
}
