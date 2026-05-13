package ru.mobileup.template.features.pokemons.presentation.details

import com.arkivanov.decompose.ComponentContext
import me.aartikov.replica.algebra.normal.withKey
import ru.mobileup.template.core.error_handling.ErrorHandler
import ru.mobileup.template.core.utils.observe
import ru.mobileup.template.features.pokemons.data.PokemonRepository
import ru.mobileup.template.features.pokemons.domain.PokemonId

class RealPokemonDetailsComponent(
    componentContext: ComponentContext,
    pokemonId: PokemonId,
    pokemonRepository: PokemonRepository,
    errorHandler: ErrorHandler
) : ComponentContext by componentContext, PokemonDetailsComponent {

    private val pokemonReplica = pokemonRepository.pokemonByIdReplica.withKey(pokemonId)

    override val pokemonState = pokemonReplica.observe(this, errorHandler)

    override fun onRefresh() {
        pokemonReplica.refresh()
    }
}
