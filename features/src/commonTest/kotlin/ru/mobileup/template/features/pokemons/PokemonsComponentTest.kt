package ru.mobileup.template.features.pokemons

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import ru.mobileup.template.core.utils.activeChild
import ru.mobileup.template.core.utils.getChildren
import ru.mobileup.template.features.pokemons.presentation.PokemonsComponent
import ru.mobileup.template.features.utils.integrationTest

class PokemonsComponentTest : FunSpec({

    integrationTest("shows pokemon list as initial screen") {
        // 🛠️ Prepare initial pokemon list response
        mockServer.enqueuePokemonList(TestPokemons.Lists.fire)
        val component = setupComponent { createPokemonsComponent(it) }

        // ✅ Verify pokemon list screen is shown initially
        component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.List>()
    }

    integrationTest("opens pokemon details when pokemon is requested from list") {
        // 🛠️ Prepare pokemon list and details responses
        mockServer.enqueuePokemonList(TestPokemons.Lists.fire)
        mockServer.enqueuePokemonDetails(TestPokemons.Details.ponyta)
        val component = setupComponent { createPokemonsComponent(it) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify pokemon list screen is active
        val listChild =
            component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.List>()

        // ▶️ Request pokemon details from the list
        listChild.component.onPokemonClick(TestPokemons.Details.ponyta.pokemonId)

        // ✅ Verify pokemon details screen is active
        val detailsChild =
            component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.Details>()

        // ▶️ Wait for pokemon details loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded pokemon details state
        detailsChild.component.pokemonState.value.data shouldBe TestPokemons.Details.ponyta.expected
    }

    integrationTest("returns to pokemon list when back is requested from details") {
        // 🛠️ Prepare pokemon list response
        mockServer.enqueuePokemonList(TestPokemons.Lists.fire)
        val component = setupComponent { createPokemonsComponent(it) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify pokemon list screen is active
        val listChild =
            component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.List>()

        // ▶️ Request pokemon details from the list
        listChild.component.onPokemonClick(TestPokemons.Details.ponyta.pokemonId)

        // ✅ Verify pokemon details screen is active
        component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.Details>()

        // ▶️ Go back from pokemon details
        component.onBack()

        // ✅ Verify pokemon list screen is active
        component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.List>()
    }

    integrationTest("does not duplicate pokemon details when same pokemon is requested twice") {
        // 🛠️ Prepare pokemon list and details response
        mockServer.enqueuePokemonList(TestPokemons.Lists.fire)
        mockServer.enqueuePokemonDetails(TestPokemons.Details.ponyta)
        val component = setupComponent { createPokemonsComponent(it) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify pokemon list screen is active
        val listChild =
            component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.List>()

        // ▶️ Request pokemon details from the list
        listChild.component.onPokemonClick(TestPokemons.Details.ponyta.pokemonId)
        runCurrent()

        // ✅ Verify pokemon details screen is active
        component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.Details>()

        // ▶️ Request the same pokemon details from the list again
        listChild.component.onPokemonClick(TestPokemons.Details.ponyta.pokemonId)
        runCurrent()

        // ✅ Verify pokemon details screen is still active
        component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.Details>()

        // ✅ Verify navigation stack has one pokemon details screen
        component.childStack
            .getChildren<PokemonsComponent.Child.Details>()
            .size shouldBe 1
    }
})
