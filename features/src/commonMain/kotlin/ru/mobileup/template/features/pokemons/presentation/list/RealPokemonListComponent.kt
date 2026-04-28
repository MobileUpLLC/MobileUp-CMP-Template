package ru.mobileup.template.features.pokemons.presentation.list

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import me.aartikov.replica.algebra.normal.withKey
import me.aartikov.replica.keyed.keepPreviousData
import ru.mobileup.template.core.error_handling.ErrorHandler
import ru.mobileup.template.core.utils.observe
import ru.mobileup.template.core.utils.persistent
import ru.mobileup.template.features.pokemons.data.PokemonRepository
import ru.mobileup.template.features.pokemons.domain.PokemonId
import ru.mobileup.template.features.pokemons.domain.PokemonType
import ru.mobileup.template.features.pokemons.domain.PokemonTypeId

class RealPokemonListComponent(
    componentContext: ComponentContext,
    private val onOutput: (PokemonListComponent.Output) -> Unit,
    pokemonRepository: PokemonRepository,
    errorHandler: ErrorHandler
) : ComponentContext by componentContext, PokemonListComponent {

    override val types = listOf(
        PokemonType.Fire,
        PokemonType.Water,
        PokemonType.Electric,
        PokemonType.Grass,
        PokemonType.Poison
    )

    override val selectedTypeId = MutableStateFlow(types[0].id)

    private val pokemonsReplica = pokemonRepository.pokemonsByTypeReplica
        .keepPreviousData()
        .withKey(selectedTypeId)

    override val pokemonsState = pokemonsReplica.observe(this, errorHandler)

    init {
        persistent(
            serializer = PersistentState.serializer(),
            save = { PersistentState(selectedTypeId.value) },
            restore = { state -> selectedTypeId.value = state.selectedTypeId }
        )
    }

    override fun onTypeClick(typeId: PokemonTypeId) {
        selectedTypeId.value = typeId
    }

    override fun onPokemonClick(pokemonId: PokemonId) {
        onOutput(PokemonListComponent.Output.PokemonDetailsRequested(pokemonId))
    }

    override fun onDialogsDemoClick() {
        onOutput(PokemonListComponent.Output.DialogsDemoRequested)
    }

    override fun onRefresh() {
        pokemonsReplica.refresh()
    }

    @Serializable
    private data class PersistentState(
        val selectedTypeId: PokemonTypeId
    )
}
