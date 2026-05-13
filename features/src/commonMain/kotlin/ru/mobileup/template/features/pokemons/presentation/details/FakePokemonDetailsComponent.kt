package ru.mobileup.template.features.pokemons.presentation.details

import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.template.core.utils.LoadableState
import ru.mobileup.template.features.pokemons.domain.DetailedPokemon

class FakePokemonDetailsComponent : PokemonDetailsComponent {

    override val pokemonState = MutableStateFlow(LoadableState(data = DetailedPokemon.MOCK))

    override fun onRefresh() = Unit
}
