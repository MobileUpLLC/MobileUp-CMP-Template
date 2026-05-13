package ru.mobileup.template.features.pokemons.domain

data class DetailedPokemon(
    val id: PokemonId,
    val name: String,
    val height: Float,
    val weight: Float,
    val imageUrl: String,
    val types: List<PokemonType>
) {
    companion object {
        val MOCK = DetailedPokemon(
            id = PokemonId("1"),
            name = "Bulbasaur",
            imageUrl = "",
            height = 0.7f,
            weight = 6.9f,
            types = listOf(PokemonType.Grass, PokemonType.Poison)
        )
    }
}