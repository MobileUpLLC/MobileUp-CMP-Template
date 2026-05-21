package ru.mobileup.template.features.permission

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import org.koin.dsl.module
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.features.permission.presentation.PermissionComponent
import ru.mobileup.template.features.permission.presentation.RealPermissionComponent

val permissionModule = module { }

fun ComponentFactory.createPermissionComponent(
    componentContext: ComponentContext
): PermissionComponent {
    return RealPermissionComponent(componentContext, get(), get(), get(), get())
}
