package ru.mobileup.template.features.pokemons.presentation.details

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.template.core.utils.LoadableState
import ru.mobileup.template.features.pokemons.domain.DetailedPokemon
import ru.mobileup.template.features.pokemons.domain.PokemonId

interface PokemonDetailsComponent {

    val pokemonId: PokemonId

    val pokemonState: StateFlow<LoadableState<DetailedPokemon>>

    fun onRefresh()
}
