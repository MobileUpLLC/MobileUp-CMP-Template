package ru.mobileup.template.core.dialog

import com.arkivanov.decompose.router.slot.ChildSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface DialogControl<C : Any, T : Any> {
    val dialogSlot: StateFlow<ChildSlot<*, T>>
    val dismissableByUser: StateFlow<Boolean>
    val dismissedEvent: Flow<Unit>

    fun show(config: C)
    fun dismiss()
}