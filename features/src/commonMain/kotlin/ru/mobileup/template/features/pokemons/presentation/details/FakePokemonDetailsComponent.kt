package ru.mobileup.template.features.pokemons.presentation.details

import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.template.core.utils.LoadableState
import ru.mobileup.template.features.pokemons.domain.DetailedPokemon
import ru.mobileup.template.features.pokemons.domain.PokemonId

class FakePokemonDetailsComponent(
    pokemon: DetailedPokemon = DetailedPokemon.FAKE
) : PokemonDetailsComponent {

    override val pokemonId: PokemonId = pokemon.id

    override val pokemonState = MutableStateFlow(LoadableState(data = pokemon))

    override fun onRefresh() = Unit
}
