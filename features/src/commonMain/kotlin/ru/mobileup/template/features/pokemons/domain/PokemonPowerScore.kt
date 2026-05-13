package ru.mobileup.template.features.pokemons.domain

data class PokemonPowerScore(
    val total: Int,
    val offense: Int,
    val defense: Int,
    val mobility: Int,
    val tier: PowerTier
)