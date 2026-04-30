package ru.mobileup.template.features.pokemons.presentation.list

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.aartikov.replica.algebra.normal.withKey
import me.aartikov.replica.keyed.keepPreviousData
import ru.mobileup.template.core.error_handling.ErrorHandler
import ru.mobileup.template.core.message.data.MessageService
import ru.mobileup.template.core.message.domain.Message
import ru.mobileup.template.core.permissions.Permission
import ru.mobileup.template.core.permissions.PermissionResult
import ru.mobileup.template.core.permissions.PermissionService
import ru.mobileup.template.core.utils.componentScope
import ru.mobileup.template.core.utils.desc
import ru.mobileup.template.core.utils.observe
import ru.mobileup.template.core.utils.persistent
import ru.mobileup.template.core.utils.plus
import ru.mobileup.template.core.utils.resourceDesc
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.pokemons_camera_permission_denied
import ru.mobileup.template.features.generated.resources.pokemons_camera_permission_granted
import ru.mobileup.template.features.pokemons.data.PokemonRepository
import ru.mobileup.template.features.pokemons.domain.PokemonId
import ru.mobileup.template.features.pokemons.domain.PokemonType
import ru.mobileup.template.features.pokemons.domain.PokemonTypeId

class RealPokemonListComponent(
    componentContext: ComponentContext,
    private val onOutput: (PokemonListComponent.Output) -> Unit,
    pokemonRepository: PokemonRepository,
    errorHandler: ErrorHandler,
    private val permissionService: PermissionService,
    private val messageService: MessageService
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

    override fun onCameraPermissionClick() {
        componentScope.launch {
            val text = when (val result = permissionService.requestPermission(Permission.Camera)) {
                PermissionResult.Granted -> Res.string.pokemons_camera_permission_granted.resourceDesc()
                is PermissionResult.Denied -> {
                    Res.string.pokemons_camera_permission_denied.resourceDesc() + " ".desc() + result.toString().desc()
                }
            }

            messageService.showMessage(Message(text = text))
        }
    }

    override fun onRefresh() {
        pokemonsReplica.refresh()
    }

    @Serializable
    private data class PersistentState(
        val selectedTypeId: PokemonTypeId
    )
}
