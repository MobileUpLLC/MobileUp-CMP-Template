package ru.mobileup.template.features.pokemons.presentation

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.template.features.pokemons.domain.DetailedPokemon
import ru.mobileup.template.features.pokemons.domain.PokemonId
import ru.mobileup.template.features.pokemons.presentation.details.FakePokemonDetailsComponent
import ru.mobileup.template.features.pokemons.presentation.details.PokemonDetailsComponent
import ru.mobileup.template.features.pokemons.presentation.list.FakePokemonListComponent
import ru.mobileup.template.features.pokemons.presentation.list.PokemonListComponent

class FakePokemonsChildComponentFactory : PokemonsChildComponentFactory {

    override fun createPokemonListComponent(
        componentContext: ComponentContext,
        onOutput: (PokemonListComponent.Output) -> Unit
    ): PokemonListComponent {
        return FakePokemonListComponent(onOutput)
    }

    override fun createPokemonDetailsComponent(
        componentContext: ComponentContext,
        pokemonId: PokemonId
    ): PokemonDetailsComponent {
        return FakePokemonDetailsComponent(DetailedPokemon.FAKE.copy(id = pokemonId))
    }
}
