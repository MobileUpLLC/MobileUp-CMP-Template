package ru.mobileup.template.features.pokemons.domain

data class PokemonBaseStats(
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val specialAttack: Int,
    val specialDefense: Int,
    val speed: Int
)