@file:Suppress("DEPRECATION")

package ru.mobileup.template.features.root.presentation

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.template.core.message.presentation.MessageComponent
import ru.mobileup.template.features.dialogs.presentation.DialogsComponent
import ru.mobileup.template.features.menu.presentation.MenuComponent
import ru.mobileup.template.features.permission.presentation.PermissionComponent
import ru.mobileup.template.features.places.presentation.PlacesComponent
import ru.mobileup.template.features.pokemons.presentation.PokemonsComponent
import ru.mobileup.template.features.settings.presentation.SettingsComponent

interface RootChildComponentFactory {

    fun createMenuComponent(
        componentContext: ComponentContext,
        onOutput: (MenuComponent.Output) -> Unit
    ): MenuComponent

    fun createPokemonsComponent(componentContext: ComponentContext): PokemonsComponent

    fun createDialogsComponent(componentContext: ComponentContext): DialogsComponent

    fun createPermissionComponent(componentContext: ComponentContext): PermissionComponent

    fun createSettingsComponent(componentContext: ComponentContext): SettingsComponent

    fun createPlacesComponent(componentContext: ComponentContext): PlacesComponent {
        return createPlacesComponent(componentContext)
    }

    fun createMessageComponent(componentContext: ComponentContext): MessageComponent
}
