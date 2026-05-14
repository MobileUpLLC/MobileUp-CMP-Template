package ru.mobileup.template.features.pokemons.details

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.mobileup.template.features.pokemons.TestPokemons
import ru.mobileup.template.features.pokemons.createPokemonDetailsComponent
import ru.mobileup.template.features.pokemons.enqueuePokemonDetails
import ru.mobileup.template.features.utils.integrationTest
import kotlin.time.Duration.Companion.seconds

class PokemonDetailsComponentTest : FunSpec({

    integrationTest("loads pokemon details successfully") {
        // 🛠️ Prepare a successful pokemon details response
        val pokemonId = TestPokemons.Details.ponyta.pokemonId
        mockServer.enqueuePokemonDetails(TestPokemons.Details.ponyta)
        val component = setupComponent { createPokemonDetailsComponent(it, pokemonId) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded pokemon details state
        component.pokemonState.value.loading shouldBe false
        component.pokemonState.value.error shouldBe null
        component.pokemonState.value.data shouldBe TestPokemons.Details.ponyta.expected
    }

    integrationTest("shows error when pokemon details loading fails") {
        // 🛠️ Prepare a failed pokemon details response
        val pokemonId = TestPokemons.Details.ponyta.pokemonId
        mockServer.enqueuePokemonDetails(TestPokemons.Details.ponyta, isError = true)
        val component = setupComponent { createPokemonDetailsComponent(it, pokemonId) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify failed pokemon details state
        component.pokemonState.value.loading shouldBe false
        component.pokemonState.value.data shouldBe null
        component.pokemonState.value.error.shouldNotBeNull()
    }

    integrationTest("reloads pokemon details after error") {
        // 🛠️ Prepare a failed initial response and a successful retry response
        val pokemonId = TestPokemons.Details.ponyta.pokemonId
        mockServer.enqueuePokemonDetails(TestPokemons.Details.ponyta, isError = true)
        mockServer.enqueuePokemonDetails(TestPokemons.Details.ponyta, delay = 1.seconds)
        val component = setupComponent { createPokemonDetailsComponent(it, pokemonId) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify failed pokemon details state
        component.pokemonState.value.loading shouldBe false
        component.pokemonState.value.data shouldBe null
        component.pokemonState.value.error.shouldNotBeNull()

        // ▶️ Refresh pokemon details
        component.onRefresh()
        runCurrent()

        // ✅ Verify loading starts again
        component.pokemonState.value.loading shouldBe true

        // ▶️ Wait for retry loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded pokemon details state after retry
        component.pokemonState.value.loading shouldBe false
        component.pokemonState.value.error shouldBe null
        component.pokemonState.value.data shouldBe TestPokemons.Details.ponyta.expected
    }

    integrationTest("shows loading during refresh and updates pokemon details") {
        // 🛠️ Prepare initial data and a delayed refresh response
        val pokemonId = TestPokemons.Details.ponyta.pokemonId
        mockServer.enqueuePokemonDetails(TestPokemons.Details.ponyta)
        mockServer.enqueuePokemonDetails(TestPokemons.Details.ponytaUpdated, delay = 1.seconds)
        val component = setupComponent { createPokemonDetailsComponent(it, pokemonId) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded pokemon details state
        component.pokemonState.value.loading shouldBe false
        component.pokemonState.value.error shouldBe null
        component.pokemonState.value.data shouldBe TestPokemons.Details.ponyta.expected

        // ▶️ Refresh pokemon details
        component.onRefresh()
        runCurrent()

        // ✅ Verify loading is shown during refresh
        component.pokemonState.value.loading shouldBe true

        // ▶️ Wait for refresh to complete
        advanceUntilIdle()

        // ✅ Verify updated pokemon details state
        component.pokemonState.value.loading shouldBe false
        component.pokemonState.value.error shouldBe null
        component.pokemonState.value.data shouldBe TestPokemons.Details.ponytaUpdated.expected
    }
})
