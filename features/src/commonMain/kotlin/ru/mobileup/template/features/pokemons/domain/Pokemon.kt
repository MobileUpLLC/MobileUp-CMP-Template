package ru.mobileup.template.features.pokemons.domain

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class PokemonId(val value: String)

data class Pokemon(
    val id: PokemonId,
    val name: String
) {
    companion object {
        val MOCK_LIST = listOf(
            Pokemon(
                id = PokemonId("1"),
                name = "Bulbasaur"
            ),
            Pokemon(
                id = PokemonId("5"),
                name = "Charmeleon"
            ),
            Pokemon(
                id = PokemonId("7"),
                name = "Squirtle"
            )
        )
    }
}