package ru.mobileup.template.features.pokemons.fixtures

import io.ktor.http.HttpStatusCode
import ru.mobileup.template.core_testing.network.HttpResponse
import ru.mobileup.template.core_testing.network.MockServer
import ru.mobileup.template.core_testing.network.RequestMatcher
import ru.mobileup.template.core_testing.network.containsPath
import ru.mobileup.template.core_testing.utils.readTestResource
import ru.mobileup.template.features.pokemons.domain.Pokemon
import ru.mobileup.template.features.pokemons.domain.PokemonId
import ru.mobileup.template.features.pokemons.domain.PokemonType
import ru.mobileup.template.features.pokemons.domain.PokemonTypeId

data class PokemonListFixture(
    val typeId: PokemonTypeId,
    val resourcePath: String,
    val domain: List<Pokemon>
) {
    companion object {
        val fire = PokemonListFixture(
            typeId = PokemonType.Fire.id,
            resourcePath = "pokemons/fire_pokemons.json",
            domain = listOf(
                Pokemon(id = PokemonId("4"), name = "Charmander"),
                Pokemon(id = PokemonId("77"), name = "Ponyta")
            )
        )

        val fireUpdated = PokemonListFixture(
            typeId = PokemonType.Fire.id,
            resourcePath = "pokemons/fire_pokemons_updated.json",
            domain = listOf(
                Pokemon(id = PokemonId("4"), name = "Charmander"),
                Pokemon(id = PokemonId("77"), name = "Ponyta updated")
            )
        )

        val water = PokemonListFixture(
            typeId = PokemonType.Water.id,
            resourcePath = "pokemons/water_pokemons.json",
            domain = listOf(
                Pokemon(id = PokemonId("7"), name = "Squirtle"),
                Pokemon(id = PokemonId("54"), name = "Psyduck")
            )
        )
    }
}

suspend fun MockServer.enqueuePokemonList(
    fixture: PokemonListFixture,
    isError: Boolean = false
) {
    enqueue(
        matcher = RequestMatcher.containsPath("type/${fixture.typeId.value}"),
        response = if (isError) {
            HttpResponse(status = HttpStatusCode.NotFound)
        } else {
            HttpResponse(readTestResource(fixture.resourcePath))
        }
    )
}
