package ru.mobileup.template.features.pokemons

import ru.mobileup.template.features.pokemons.domain.DetailedPokemon
import ru.mobileup.template.features.pokemons.domain.Pokemon
import ru.mobileup.template.features.pokemons.domain.PokemonId
import ru.mobileup.template.features.pokemons.domain.PokemonType
import ru.mobileup.template.features.pokemons.domain.PokemonTypeId
import ru.mobileup.template.features.utils.readTestResource

object TestPokemons {
    val charmanderId = PokemonId("4")
    val ponytaId = PokemonId("77")
    val ponytaTypeId = PokemonTypeId("77")
    val fireTypeId = PokemonType.Fire.id
    val waterTypeId = PokemonType.Water.id
    val squirtleId = PokemonId("7")
    val psyduckId = PokemonId("54")

    suspend fun firePokemonsJson(): String = readTestResource("responses/fire_pokemons.json")

    val firePokemons = listOf(
        Pokemon(id = charmanderId, name = "Charmander"),
        Pokemon(id = ponytaId, name = "Ponyta")
    )

    suspend fun waterPokemonsJson(): String = readTestResource("responses/water_pokemons.json")

    val waterPokemons = listOf(
        Pokemon(id = squirtleId, name = "Squirtle"),
        Pokemon(id = psyduckId, name = "Psyduck")
    )

    suspend fun detailedPonytaJson(): String = readTestResource("responses/detailed_ponyta.json")
    suspend fun detailedPonytaUpdatedJson(): String =
        readTestResource("responses/detailed_ponyta_updated.json")

    val detailedPonyta = DetailedPokemon(
        id = ponytaId,
        name = "Ponyta",
        height = 1f,
        weight = 30f,
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${ponytaId.value}.png",
        types = listOf(PokemonType(ponytaTypeId, "Fire"))
    )

    val detailedPonytaUpdated = DetailedPokemon(
        id = ponytaId,
        name = "Ponyta updated",
        height = 1.1f,
        weight = 31f,
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${ponytaId.value}.png",
        types = listOf(PokemonType(ponytaTypeId, "Fire"))
    )
}
