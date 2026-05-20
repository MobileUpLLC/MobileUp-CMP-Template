# Pokemon List Component Test

Use this as the source example for a screen component test that loads data, emits output, handles
errors, refreshes, and changes a selected type while keeping previous data.

```kotlin
package <package_name>.features.pokemons.presentation.list

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import <package_name>.core_testing.utils.OutputCapturer
import <package_name>.features.pokemons.fixtures.PokemonDetailsFixture
import <package_name>.features.pokemons.fixtures.PokemonListFixture
import <package_name>.features.pokemons.createPokemonListComponent
import <package_name>.features.pokemons.fixtures.enqueuePokemonList
import <package_name>.features.utils.componentTest

class PokemonListComponentTest : FunSpec({

    componentTest("loads pokemon list successfully") {
        // 🛠️ Prepare pokemon list data for initial loading
        val pokemons = PokemonListFixture.fire
        mockServer.enqueuePokemonList(pokemons)
        val component = setupComponent { createPokemonListComponent(it, {}) }

        // ✅ Verify initial loading is shown
        component.pokemonsState.value.loading shouldBe true

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe pokemons.domain
    }

    componentTest("emits pokemon details output when a pokemon is clicked") {
        // 🛠️ Prepare pokemon list data for initial loading
        val pokemons = PokemonListFixture.fire
        val pokemon = PokemonDetailsFixture.ponyta
        mockServer.enqueuePokemonList(pokemons)
        val capturer = OutputCapturer<PokemonListComponent.Output>()
        val component = setupComponent { createPokemonListComponent(it, capturer) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ▶️ Click a pokemon item in the list
        component.onPokemonClick(pokemon.id)

        // ✅ Verify the pokemon details output is emitted
        capturer.last shouldBe PokemonListComponent.Output.PokemonDetailsRequested(pokemon.id)
    }

    componentTest("shows error when pokemon list loading fails") {
        // 🛠️ Prepare failed pokemon list loading
        val pokemons = PokemonListFixture.fire
        mockServer.enqueuePokemonList(pokemons, isError = true)
        val component = setupComponent { createPokemonListComponent(it, {}) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify failed pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.data shouldBe null
        component.pokemonsState.value.error.shouldNotBeNull()
    }

    componentTest("shows loading during refresh and updates pokemon list") {
        // 🛠️ Prepare initial and refreshed pokemon list data
        val initialPokemons = PokemonListFixture.fire
        val updatedPokemons = PokemonListFixture.fireUpdated
        mockServer.enqueuePokemonList(initialPokemons)
        mockServer.enqueuePokemonList(updatedPokemons)
        val component = setupComponent { createPokemonListComponent(it, {}) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe initialPokemons.domain

        // ▶️ Refresh pokemon list
        component.onRefresh()

        // ✅ Verify loading is shown during refresh
        component.pokemonsState.value.loading shouldBe true

        // ▶️ Wait for refresh to complete
        advanceUntilIdle()

        // ✅ Verify updated pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe updatedPokemons.domain
    }

    componentTest("loads selected type pokemon list while keeping previous data") {
        // 🛠️ Prepare default and selected type pokemon list data
        val initialPokemons = PokemonListFixture.fire
        val selectedTypePokemons = PokemonListFixture.water
        mockServer.enqueuePokemonList(initialPokemons)
        mockServer.enqueuePokemonList(selectedTypePokemons)
        val component = setupComponent { createPokemonListComponent(it, {}) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded default pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe initialPokemons.domain

        // ▶️ Select another pokemon type
        component.onTypeClick(selectedTypePokemons.typeId)

        // ✅ Verify selected pokemon type is changed
        component.selectedTypeId.value shouldBe selectedTypePokemons.typeId

        // ✅ Verify previous pokemon list is kept while selected type is loading
        component.pokemonsState.value.loading shouldBe true
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe initialPokemons.domain

        // ▶️ Wait for selected type loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded selected pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe selectedTypePokemons.domain
    }

    componentTest("reloads pokemon list after error") {
        // 🛠️ Prepare failed initial loading and successful retry
        val pokemons = PokemonListFixture.fire
        mockServer.enqueuePokemonList(pokemons, isError = true)
        mockServer.enqueuePokemonList(pokemons)
        val component = setupComponent { createPokemonListComponent(it, {}) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify failed pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.data shouldBe null
        component.pokemonsState.value.error.shouldNotBeNull()

        // ▶️ Refresh pokemon list
        component.onRefresh()

        // ✅ Verify loading starts again
        component.pokemonsState.value.loading shouldBe true

        // ▶️ Wait for retry loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded pokemon list state after retry
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe pokemons.domain
    }
})
```
