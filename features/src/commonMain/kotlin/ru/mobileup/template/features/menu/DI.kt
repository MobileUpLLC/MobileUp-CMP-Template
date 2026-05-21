package ru.mobileup.template.features.menu

import com.arkivanov.decompose.ComponentContext
import org.koin.dsl.module
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.features.menu.presentation.MenuComponent
import ru.mobileup.template.features.menu.presentation.RealMenuComponent

val menuModule = module { }

fun ComponentFactory.createMenuComponent(
    componentContext: ComponentContext,
    onOutput: (MenuComponent.Output) -> Unit
): MenuComponent {
    return RealMenuComponent(componentContext, onOutput)
}
