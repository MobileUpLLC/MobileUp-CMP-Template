package ru.mobileup.template.features.root.presentation

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.core.createMessageComponent
import ru.mobileup.template.core.message.presentation.MessageComponent
import ru.mobileup.template.features.pokemons.createPokemonsComponent
import ru.mobileup.template.features.pokemons.presentation.PokemonsComponent

class RealRootChildComponentFactory(
    private val componentFactory: ComponentFactory
) : RootChildComponentFactory {

    override fun createPokemonsComponent(componentContext: ComponentContext): PokemonsComponent {
        return componentFactory.createPokemonsComponent(componentContext)
    }

    override fun createMessageComponent(componentContext: ComponentContext): MessageComponent {
        return componentFactory.createMessageComponent(componentContext)
    }
}
