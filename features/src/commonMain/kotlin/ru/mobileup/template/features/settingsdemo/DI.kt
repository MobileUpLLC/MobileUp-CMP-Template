package ru.mobileup.template.features.settingsdemo

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import org.koin.dsl.module
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.features.settingsdemo.presentation.RealSettingsDemoComponent
import ru.mobileup.template.features.settingsdemo.presentation.SettingsDemoComponent

val settingsDemoModule = module { }

fun ComponentFactory.createSettingsDemoComponent(
    componentContext: ComponentContext
): SettingsDemoComponent {
    return RealSettingsDemoComponent(componentContext, get(), get())
}
