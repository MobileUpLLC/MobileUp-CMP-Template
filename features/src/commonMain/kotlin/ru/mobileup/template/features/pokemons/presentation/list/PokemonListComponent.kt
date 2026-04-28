package ru.mobileup.template.features.pokemons.presentation.list

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.template.core.utils.LoadableState
import ru.mobileup.template.features.pokemons.domain.Pokemon
import ru.mobileup.template.features.pokemons.domain.PokemonId
import ru.mobileup.template.features.pokemons.domain.PokemonType
import ru.mobileup.template.features.pokemons.domain.PokemonTypeId

interface PokemonListComponent {

    val types: List<PokemonType>

    val selectedTypeId: StateFlow<PokemonTypeId>

    val pokemonsState: StateFlow<LoadableState<List<Pokemon>>>

    fun onTypeClick(typeId: PokemonTypeId)

    fun onPokemonClick(pokemonId: PokemonId)

    fun onDialogsDemoClick()

    fun onRefresh()

    sealed interface Output {
        data class PokemonDetailsRequested(val pokemonId: PokemonId) : Output
        data object DialogsDemoRequested : Output
    }
}
