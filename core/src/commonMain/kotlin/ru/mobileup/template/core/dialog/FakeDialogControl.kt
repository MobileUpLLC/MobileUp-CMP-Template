package ru.mobileup.template.core.dialog

import com.arkivanov.decompose.router.slot.ChildSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.template.core.utils.createFakeChildSlot

fun <C : Any, T : Any> fakeDialogControl(config: C, component: T): DialogControl<C, T> {
    return FakeDialogControl(config, component)
}

fun <T : Any> fakeDialogControl(component: T): DialogControl<*, T> {
    return FakeDialogControl("<fake>", component)
}

private class FakeDialogControl<C : Any, T : Any>(config: C, component: T) : DialogControl<C, T> {

    override val dialogSlot: StateFlow<ChildSlot<*, T>> = createFakeChildSlot(config, component)

    override val dismissedEvent: Flow<Unit> = MutableSharedFlow()

    override val dismissableByUser: StateFlow<Boolean> = MutableStateFlow(true)

    override fun show(config: C) = Unit

    override fun dismiss() = Unit
}