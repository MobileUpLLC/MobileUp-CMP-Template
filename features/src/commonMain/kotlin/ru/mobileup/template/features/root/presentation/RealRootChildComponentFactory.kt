package ru.mobileup.template.features.root.presentation

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.core.createMessageComponent
import ru.mobileup.template.core.message.presentation.MessageComponent
import ru.mobileup.template.features.dialogs.createDialogsComponent
import ru.mobileup.template.features.dialogs.presentation.DialogsComponent
import ru.mobileup.template.features.menu.createMenuComponent
import ru.mobileup.template.features.menu.presentation.MenuComponent
import ru.mobileup.template.features.permission.createPermissionComponent
import ru.mobileup.template.features.permission.presentation.PermissionComponent
import ru.mobileup.template.features.pokemons.createPokemonsComponent
import ru.mobileup.template.features.pokemons.presentation.PokemonsComponent
import ru.mobileup.template.features.pomodoro.createPomodoroComponent
import ru.mobileup.template.features.pomodoro.presentation.PomodoroComponent
import ru.mobileup.template.features.settings.createSettingsComponent
import ru.mobileup.template.features.settings.presentation.SettingsComponent

class RealRootChildComponentFactory(
    private val componentFactory: ComponentFactory
) : RootChildComponentFactory {

    override fun createMenuComponent(
        componentContext: ComponentContext,
        onOutput: (MenuComponent.Output) -> Unit
    ): MenuComponent {
        return componentFactory.createMenuComponent(componentContext, onOutput)
    }

    override fun createPokemonsComponent(componentContext: ComponentContext): PokemonsComponent {
        return componentFactory.createPokemonsComponent(componentContext)
    }

    override fun createDialogsComponent(componentContext: ComponentContext): DialogsComponent {
        return componentFactory.createDialogsComponent(componentContext)
    }

    override fun createPermissionComponent(componentContext: ComponentContext): PermissionComponent {
        return componentFactory.createPermissionComponent(componentContext)
    }

    override fun createSettingsComponent(componentContext: ComponentContext): SettingsComponent {
        return componentFactory.createSettingsComponent(componentContext)
    }

    override fun createPomodoroComponent(componentContext: ComponentContext): PomodoroComponent {
        return componentFactory.createPomodoroComponent(componentContext)
    }

    override fun createMessageComponent(componentContext: ComponentContext): MessageComponent {
        return componentFactory.createMessageComponent(componentContext)
    }
}
