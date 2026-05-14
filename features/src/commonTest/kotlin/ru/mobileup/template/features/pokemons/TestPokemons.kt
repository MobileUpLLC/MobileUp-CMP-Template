package ru.mobileup.template.features.pokemons

import io.ktor.http.HttpStatusCode
import ru.mobileup.template.core_testing.network.HttpResponse
import ru.mobileup.template.core_testing.network.MockServer
import ru.mobileup.template.core_testing.network.RequestMatcher
import ru.mobileup.template.core_testing.network.containsPath
import ru.mobileup.template.core_testing.utils.readTestResource
import ru.mobileup.template.features.pokemons.domain.DetailedPokemon
import ru.mobileup.template.features.pokemons.domain.Pokemon
import ru.mobileup.template.features.pokemons.domain.PokemonId
import ru.mobileup.template.features.pokemons.domain.PokemonType
import ru.mobileup.template.features.pokemons.domain.PokemonTypeId
import kotlin.time.Duration

object TestPokemons {

    data class PokemonListFixture(
        val typeId: PokemonTypeId,
        val resourcePath: String,
        val expected: List<Pokemon>
    )

    data class PokemonDetailsFixture(
        val pokemonId: PokemonId,
        val resourcePath: String,
        val expected: DetailedPokemon
    )

    object Lists {
        val fire = PokemonListFixture(
            typeId = PokemonType.Fire.id,
            resourcePath = "responses/fire_pokemons.json",
            expected = listOf(
                Pokemon(id = PokemonId("4"), name = "Charmander"),
                Pokemon(id = PokemonId("77"), name = "Ponyta")
            )
        )

        val fireUpdated = PokemonListFixture(
            typeId = PokemonType.Fire.id,
            resourcePath = "responses/fire_pokemons_updated.json",
            expected = listOf(
                Pokemon(id = PokemonId("4"), name = "Charmander"),
                Pokemon(id = PokemonId("77"), name = "Ponyta updated")
            )
        )

        val water = PokemonListFixture(
            typeId = PokemonType.Water.id,
            resourcePath = "responses/water_pokemons.json",
            expected = listOf(
                Pokemon(id = PokemonId("7"), name = "Squirtle"),
                Pokemon(id = PokemonId("54"), name = "Psyduck")
            )
        )
    }

    object Details {
        val ponyta = PokemonDetailsFixture(
            pokemonId = PokemonId("77"),
            resourcePath = "responses/detailed_ponyta.json",
            expected = DetailedPokemon(
                id = PokemonId("77"),
                name = "Ponyta",
                height = 1f,
                weight = 30f,
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/77.png",
                types = listOf(PokemonType(PokemonTypeId("77"), "Fire"))
            )
        )

        val ponytaUpdated = PokemonDetailsFixture(
            pokemonId = PokemonId("77"),
            resourcePath = "responses/detailed_ponyta_updated.json",
            expected = DetailedPokemon(
                id = PokemonId("77"),
                name = "Ponyta updated",
                height = 1.1f,
                weight = 31f,
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/77.png",
                types = listOf(PokemonType(PokemonTypeId("77"), "Fire"))
            )
        )
    }
}

suspend fun MockServer.enqueuePokemonList(
    fixture: TestPokemons.PokemonListFixture,
    delay: Duration = Duration.ZERO,
    isError: Boolean = false
) {
    enqueue(
        matcher = RequestMatcher.containsPath("type/${fixture.typeId.value}"),
        response = if (isError) {
            HttpResponse(status = HttpStatusCode.NotFound, delay = delay)
        } else {
            HttpResponse(readTestResource(fixture.resourcePath), delay = delay)
        }
    )
}

suspend fun MockServer.enqueuePokemonDetails(
    fixture: TestPokemons.PokemonDetailsFixture,
    delay: Duration = Duration.ZERO,
    isError: Boolean = false
) {
    enqueue(
        matcher = RequestMatcher.containsPath("pokemon/${fixture.pokemonId.value}"),
        response = if (isError) {
            HttpResponse(status = HttpStatusCode.NotFound, delay = delay)
        } else {
            HttpResponse(readTestResource(fixture.resourcePath), delay = delay)
        }
    )
}
