package ru.mobileup.template.features.pokemons.presentation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import ru.mobileup.template.core.utils.activeChild
import ru.mobileup.template.features.pokemons.fixtures.PokemonDetailsFixture
import ru.mobileup.template.features.pokemons.fixtures.PokemonListFixture
import ru.mobileup.template.features.pokemons.createPokemonsComponent
import ru.mobileup.template.features.pokemons.fixtures.enqueuePokemonDetails
import ru.mobileup.template.features.pokemons.fixtures.enqueuePokemonList
import ru.mobileup.template.features.utils.integrationTest

class PokemonsComponentTest : FunSpec({

    integrationTest("shows pokemon list as initial screen") {
        // 🛠️ Prepare pokemon list data for initial screen
        val pokemons = PokemonListFixture.fire
        mockServer.enqueuePokemonList(pokemons)
        val component = setupComponent { createPokemonsComponent(it) }

        // ✅ Verify pokemon list screen is shown initially
        component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.List>()
    }

    integrationTest("opens pokemon details when pokemon is requested from list") {
        // 🛠️ Prepare pokemon list and details data
        val pokemons = PokemonListFixture.fire
        val pokemon = PokemonDetailsFixture.ponyta
        mockServer.enqueuePokemonList(pokemons)
        mockServer.enqueuePokemonDetails(pokemon)
        val component = setupComponent { createPokemonsComponent(it) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify pokemon list screen is active
        val listChild =
            component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.List>()

        // ▶️ Request pokemon details from the list
        listChild.component.onPokemonClick(pokemon.id)

        // ✅ Verify pokemon details screen is active
        component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.Details>()
    }
})
