package ru.mobileup.template.features.root

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.features.root.presentation.RealRootChildComponentFactory
import ru.mobileup.template.features.root.presentation.RealRootComponent
import ru.mobileup.template.features.root.presentation.RootChildComponentFactory
import ru.mobileup.template.features.root.presentation.RootComponent

fun ComponentFactory.createRootComponent(
    componentContext: ComponentContext,
    childComponentFactory: RootChildComponentFactory = RealRootChildComponentFactory(this)
): RootComponent {
    return RealRootComponent(componentContext, childComponentFactory)
}
