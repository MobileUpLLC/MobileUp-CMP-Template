package ru.mobileup.template.features.pokemons.fixtures

import io.ktor.http.HttpStatusCode
import ru.mobileup.template.core_testing.network.HttpResponse
import ru.mobileup.template.core_testing.network.MockServer
import ru.mobileup.template.core_testing.network.RequestMatcher
import ru.mobileup.template.core_testing.network.containsPath
import ru.mobileup.template.core_testing.utils.readTestResource
import ru.mobileup.template.features.pokemons.domain.DetailedPokemon
import ru.mobileup.template.features.pokemons.domain.PokemonId
import ru.mobileup.template.features.pokemons.domain.PokemonType

data class PokemonDetailsFixture(
    val resourcePath: String,
    val domain: DetailedPokemon
) {
    val id get() = domain.id

    companion object {
        val ponyta = PokemonDetailsFixture(
            resourcePath = "pokemons/ponyta_details.json",
            domain = DetailedPokemon(
                id = PokemonId("77"),
                name = "Ponyta",
                height = 1f,
                weight = 30f,
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/77.png",
                types = listOf(PokemonType.Fire)
            )
        )

        val ponytaUpdated = PokemonDetailsFixture(
            resourcePath = "pokemons/ponyta_details_updated.json",
            domain = DetailedPokemon(
                id = PokemonId("77"),
                name = "Ponyta updated",
                height = 1.1f,
                weight = 31f,
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/77.png",
                types = listOf(PokemonType.Fire)
            )
        )
    }
}

suspend fun MockServer.enqueuePokemonDetails(
    fixture: PokemonDetailsFixture,
    isError: Boolean = false
) {
    enqueue(
        matcher = RequestMatcher.containsPath("pokemon/${fixture.id.value}"),
        response = if (isError) {
            HttpResponse(status = HttpStatusCode.NotFound)
        } else {
            HttpResponse(readTestResource(fixture.resourcePath))
        }
    )
}
