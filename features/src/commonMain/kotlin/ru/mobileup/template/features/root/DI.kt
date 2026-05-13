package ru.mobileup.template.features.root

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.features.root.presentation.RealRootComponent
import ru.mobileup.template.features.root.presentation.RootComponent

fun ComponentFactory.createRootComponent(componentContext: ComponentContext): RootComponent {
    return RealRootComponent(componentContext, get())
}