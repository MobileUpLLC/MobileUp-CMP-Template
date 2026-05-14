package ru.mobileup.template.features.pokemons.list

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.mobileup.template.core_testing.utils.OutputCapturer
import ru.mobileup.template.features.pokemons.TestPokemons
import ru.mobileup.template.features.pokemons.createPokemonListComponent
import ru.mobileup.template.features.pokemons.domain.PokemonType
import ru.mobileup.template.features.pokemons.enqueuePokemonList
import ru.mobileup.template.features.pokemons.presentation.list.PokemonListComponent
import ru.mobileup.template.features.utils.integrationTest
import kotlin.time.Duration.Companion.seconds

class PokemonListComponentTest : FunSpec({

    integrationTest("loads pokemon list successfully") {
        // 🛠️ Prepare a successful pokemon list response
        mockServer.enqueuePokemonList(TestPokemons.Lists.fire)
        val component = setupComponent { createPokemonListComponent(it, {}) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe TestPokemons.Lists.fire.expected
    }

    integrationTest("emits pokemon details output when a pokemon is clicked") {
        // 🛠️ Prepare a successful pokemon list response
        mockServer.enqueuePokemonList(TestPokemons.Lists.fire)
        val capturer = OutputCapturer<PokemonListComponent.Output>()
        val component = setupComponent { createPokemonListComponent(it, capturer) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ▶️ Click a pokemon item in the list
        val pokemonId = TestPokemons.Details.ponyta.pokemonId
        component.onPokemonClick(pokemonId)

        // ✅ Verify the pokemon details output is emitted
        capturer.last shouldBe PokemonListComponent.Output.PokemonDetailsRequested(pokemonId)
    }

    integrationTest("shows error when pokemon list loading fails") {
        // 🛠️ Prepare a failed pokemon list response
        mockServer.enqueuePokemonList(TestPokemons.Lists.fire, isError = true)
        val component = setupComponent { createPokemonListComponent(it, {}) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify failed pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.data shouldBe null
        component.pokemonsState.value.error.shouldNotBeNull()
    }

    integrationTest("shows loading during refresh and updates pokemon list") {
        // 🛠️ Prepare initial data and a delayed refresh response
        mockServer.enqueuePokemonList(TestPokemons.Lists.fire)
        mockServer.enqueuePokemonList(TestPokemons.Lists.fireUpdated, delay = 1.seconds)
        val component = setupComponent { createPokemonListComponent(it, {}) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe TestPokemons.Lists.fire.expected

        // ▶️ Refresh pokemon list
        component.onRefresh()
        runCurrent()

        // ✅ Verify loading is shown during refresh
        component.pokemonsState.value.loading shouldBe true

        // ▶️ Wait for refresh to complete
        advanceUntilIdle()

        // ✅ Verify updated pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe TestPokemons.Lists.fireUpdated.expected
    }

    integrationTest("loads selected type pokemon list while keeping previous data") {
        // 🛠️ Prepare successful default and delayed selected pokemon list responses
        mockServer.enqueuePokemonList(TestPokemons.Lists.fire)
        mockServer.enqueuePokemonList(TestPokemons.Lists.water, delay = 1.seconds)
        val component = setupComponent { createPokemonListComponent(it, {}) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded default pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe TestPokemons.Lists.fire.expected

        // ▶️ Select another pokemon type
        component.onTypeClick(PokemonType.Water.id)
        runCurrent()

        // ✅ Verify selected pokemon type is changed
        component.selectedTypeId.value shouldBe PokemonType.Water.id

        // ✅ Verify previous pokemon list is kept while selected type is loading
        component.pokemonsState.value.loading shouldBe true
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe TestPokemons.Lists.fire.expected

        // ▶️ Wait for selected type loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded selected pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe TestPokemons.Lists.water.expected
    }

    integrationTest("reloads pokemon list after error") {
        // 🛠️ Prepare a failed initial response and a successful retry response
        mockServer.enqueuePokemonList(TestPokemons.Lists.fire, isError = true)
        mockServer.enqueuePokemonList(TestPokemons.Lists.fire, delay = 1.seconds)
        val component = setupComponent { createPokemonListComponent(it, {}) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify failed pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.data shouldBe null
        component.pokemonsState.value.error.shouldNotBeNull()

        // ▶️ Refresh pokemon list
        component.onRefresh()
        runCurrent()

        // ✅ Verify loading starts again
        component.pokemonsState.value.loading shouldBe true

        // ▶️ Wait for retry loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded pokemon list state after retry
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe TestPokemons.Lists.fire.expected
    }
})
