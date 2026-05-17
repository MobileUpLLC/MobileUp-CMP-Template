package ru.mobileup.template.features.pokemons.presentation.details

import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.template.core.utils.LoadableState
import ru.mobileup.template.features.pokemons.domain.DetailedPokemon
import ru.mobileup.template.features.pokemons.domain.PokemonId

class FakePokemonDetailsComponent(
    override val pokemonId: PokemonId = DetailedPokemon.MOCK.id
) : PokemonDetailsComponent {

    override val pokemonState = MutableStateFlow(LoadableState(data = DetailedPokemon.MOCK))

    override fun onRefresh() = Unit
}
