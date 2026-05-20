# Pokemon Details Component Test

Use this as the source example for a details screen component test with loading, error, retry, and
refresh scenarios.

```kotlin
package <package_name>.features.pokemons.presentation.details

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import <package_name>.features.pokemons.fixtures.PokemonDetailsFixture
import <package_name>.features.pokemons.createPokemonDetailsComponent
import <package_name>.features.pokemons.fixtures.enqueuePokemonDetails
import <package_name>.features.utils.componentTest

class PokemonDetailsComponentTest : FunSpec({

    componentTest("loads pokemon details successfully") {
        // 🛠️ Prepare pokemon details data for initial loading
        val pokemon = PokemonDetailsFixture.ponyta
        mockServer.enqueuePokemonDetails(pokemon)
        val component = setupComponent { createPokemonDetailsComponent(it, pokemon.id) }

        // ✅ Verify initial loading is shown
        component.pokemonState.value.loading shouldBe true

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded pokemon details state
        component.pokemonState.value.loading shouldBe false
        component.pokemonState.value.error shouldBe null
        component.pokemonState.value.data shouldBe pokemon.domain
    }

    componentTest("shows error when pokemon details loading fails") {
        // 🛠️ Prepare failed pokemon details loading
        val pokemon = PokemonDetailsFixture.ponyta
        mockServer.enqueuePokemonDetails(pokemon, isError = true)
        val component = setupComponent { createPokemonDetailsComponent(it, pokemon.id) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify failed pokemon details state
        component.pokemonState.value.loading shouldBe false
        component.pokemonState.value.data shouldBe null
        component.pokemonState.value.error.shouldNotBeNull()
    }

    componentTest("reloads pokemon details after error") {
        // 🛠️ Prepare failed initial loading and successful retry
        val pokemon = PokemonDetailsFixture.ponyta
        mockServer.enqueuePokemonDetails(pokemon, isError = true)
        mockServer.enqueuePokemonDetails(pokemon)
        val component = setupComponent { createPokemonDetailsComponent(it, pokemon.id) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify failed pokemon details state
        component.pokemonState.value.loading shouldBe false
        component.pokemonState.value.data shouldBe null
        component.pokemonState.value.error.shouldNotBeNull()

        // ▶️ Refresh pokemon details
        component.onRefresh()

        // ✅ Verify loading starts again
        component.pokemonState.value.loading shouldBe true

        // ▶️ Wait for retry loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded pokemon details state after retry
        component.pokemonState.value.loading shouldBe false
        component.pokemonState.value.error shouldBe null
        component.pokemonState.value.data shouldBe pokemon.domain
    }

    componentTest("shows loading during refresh and updates pokemon details") {
        // 🛠️ Prepare initial and refreshed pokemon details data
        val initialPokemon = PokemonDetailsFixture.ponyta
        val updatedPokemon = PokemonDetailsFixture.ponytaUpdated
        mockServer.enqueuePokemonDetails(initialPokemon)
        mockServer.enqueuePokemonDetails(updatedPokemon)
        val component = setupComponent { createPokemonDetailsComponent(it, initialPokemon.id) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded pokemon details state
        component.pokemonState.value.loading shouldBe false
        component.pokemonState.value.error shouldBe null
        component.pokemonState.value.data shouldBe initialPokemon.domain

        // ▶️ Refresh pokemon details
        component.onRefresh()

        // ✅ Verify loading is shown during refresh
        component.pokemonState.value.loading shouldBe true

        // ▶️ Wait for refresh to complete
        advanceUntilIdle()

        // ✅ Verify updated pokemon details state
        component.pokemonState.value.loading shouldBe false
        component.pokemonState.value.error shouldBe null
        component.pokemonState.value.data shouldBe updatedPokemon.domain
    }
})
```
