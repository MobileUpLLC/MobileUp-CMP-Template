package ru.mobileup.template.features.root.presentation

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.template.core.message.presentation.FakeMessageComponent
import ru.mobileup.template.core.message.presentation.MessageComponent
import ru.mobileup.template.features.dialogs.presentation.DialogsComponent
import ru.mobileup.template.features.dialogs.presentation.FakeDialogsComponent
import ru.mobileup.template.features.menu.presentation.FakeMenuComponent
import ru.mobileup.template.features.menu.presentation.MenuComponent
import ru.mobileup.template.features.permission.presentation.FakePermissionComponent
import ru.mobileup.template.features.permission.presentation.PermissionComponent
import ru.mobileup.template.features.pokemons.presentation.FakePokemonsComponent
import ru.mobileup.template.features.pokemons.presentation.PokemonsComponent
import ru.mobileup.template.features.settings.presentation.FakeSettingsComponent
import ru.mobileup.template.features.settings.presentation.SettingsComponent

class FakeRootChildComponentFactory : RootChildComponentFactory {

    override fun createMenuComponent(
        componentContext: ComponentContext,
        onOutput: (MenuComponent.Output) -> Unit
    ): MenuComponent {
        return FakeMenuComponent(onOutput)
    }

    override fun createPokemonsComponent(componentContext: ComponentContext): PokemonsComponent {
        return FakePokemonsComponent()
    }

    override fun createDialogsComponent(componentContext: ComponentContext): DialogsComponent {
        return FakeDialogsComponent()
    }

    override fun createPermissionComponent(componentContext: ComponentContext): PermissionComponent {
        return FakePermissionComponent()
    }

    override fun createSettingsComponent(componentContext: ComponentContext): SettingsComponent {
        return FakeSettingsComponent()
    }

    override fun createMessageComponent(componentContext: ComponentContext): MessageComponent {
        return FakeMessageComponent()
    }
}
