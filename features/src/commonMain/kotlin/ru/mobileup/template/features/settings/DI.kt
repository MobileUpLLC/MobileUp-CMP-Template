package ru.mobileup.template.features.settings

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import org.koin.dsl.module
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.features.settings.presentation.RealSettingsComponent
import ru.mobileup.template.features.settings.presentation.SettingsComponent

val settingsModule = module { }

fun ComponentFactory.createSettingsComponent(
    componentContext: ComponentContext
): SettingsComponent {
    return RealSettingsComponent(componentContext, get(), get())
}
