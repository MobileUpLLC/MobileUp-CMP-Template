package ru.mobileup.template.features.pokemons.presentation

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.template.core_testing.utils.OutputEmitter
import ru.mobileup.template.features.pokemons.domain.PokemonId
import ru.mobileup.template.features.pokemons.presentation.details.FakePokemonDetailsComponent
import ru.mobileup.template.features.pokemons.presentation.details.PokemonDetailsComponent
import ru.mobileup.template.features.pokemons.presentation.list.FakePokemonListComponent
import ru.mobileup.template.features.pokemons.presentation.list.PokemonListComponent

class TestPokemonsChildComponentFactory : PokemonsChildComponentFactory {

    val listOutput = OutputEmitter<PokemonListComponent.Output>()

    override fun createPokemonListComponent(
        componentContext: ComponentContext,
        onOutput: (PokemonListComponent.Output) -> Unit
    ): PokemonListComponent {
        listOutput.bind(onOutput)
        return FakePokemonListComponent()
    }

    override fun createPokemonDetailsComponent(
        componentContext: ComponentContext,
        pokemonId: PokemonId
    ): PokemonDetailsComponent {
        return FakePokemonDetailsComponent(pokemonId)
    }
}
