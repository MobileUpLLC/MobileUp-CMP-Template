package ru.mobileup.template.features.pokemons.presentation

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.features.pokemons.createPokemonDetailsComponent
import ru.mobileup.template.features.pokemons.createPokemonListComponent
import ru.mobileup.template.features.pokemons.domain.PokemonId
import ru.mobileup.template.features.pokemons.presentation.details.PokemonDetailsComponent
import ru.mobileup.template.features.pokemons.presentation.list.PokemonListComponent

class RealPokemonsChildComponentFactory(
    private val componentFactory: ComponentFactory
) : PokemonsChildComponentFactory {

    override fun createPokemonListComponent(
        componentContext: ComponentContext,
        onOutput: (PokemonListComponent.Output) -> Unit
    ): PokemonListComponent {
        return componentFactory.createPokemonListComponent(componentContext, onOutput)
    }

    override fun createPokemonDetailsComponent(
        componentContext: ComponentContext,
        pokemonId: PokemonId
    ): PokemonDetailsComponent {
        return componentFactory.createPokemonDetailsComponent(componentContext, pokemonId)
    }
}
