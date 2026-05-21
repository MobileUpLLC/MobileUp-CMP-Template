package ru.mobileup.template.features.dialogs

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import org.koin.dsl.module
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.features.dialogs.presentation.DialogsComponent
import ru.mobileup.template.features.dialogs.presentation.RealDialogsComponent

val dialogsModule = module { }

fun ComponentFactory.createDialogsComponent(
    componentContext: ComponentContext
): DialogsComponent {
    return RealDialogsComponent(componentContext, get())
}
