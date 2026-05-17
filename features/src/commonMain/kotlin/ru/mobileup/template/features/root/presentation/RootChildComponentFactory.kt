package ru.mobileup.template.features.root.presentation

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.template.core.message.presentation.MessageComponent
import ru.mobileup.template.features.pokemons.presentation.PokemonsComponent

interface RootChildComponentFactory {

    fun createPokemonsComponent(componentContext: ComponentContext): PokemonsComponent

    fun createMessageComponent(componentContext: ComponentContext): MessageComponent
}
