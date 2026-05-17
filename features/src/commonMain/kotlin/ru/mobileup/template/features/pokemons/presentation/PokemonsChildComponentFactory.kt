package ru.mobileup.template.features.pokemons.presentation

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.template.features.pokemons.domain.PokemonId
import ru.mobileup.template.features.pokemons.presentation.details.PokemonDetailsComponent
import ru.mobileup.template.features.pokemons.presentation.list.PokemonListComponent

interface PokemonsChildComponentFactory {

    fun createPokemonListComponent(
        componentContext: ComponentContext,
        onOutput: (PokemonListComponent.Output) -> Unit
    ): PokemonListComponent

    fun createPokemonDetailsComponent(
        componentContext: ComponentContext,
        pokemonId: PokemonId
    ): PokemonDetailsComponent
}
