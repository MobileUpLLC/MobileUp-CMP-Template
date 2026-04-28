package ru.mobileup.template.features.pokemons.list

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import ru.mobileup.template.core_testing.network.HttpResponse
import ru.mobileup.template.core_testing.network.RequestMatcher
import ru.mobileup.template.core_testing.network.containsPath
import ru.mobileup.template.core_testing.utils.OutputCapturer
import ru.mobileup.template.features.pokemons.TestPokemons
import ru.mobileup.template.features.pokemons.createPokemonListComponent
import ru.mobileup.template.features.pokemons.domain.PokemonType
import ru.mobileup.template.features.pokemons.presentation.list.PokemonListComponent
import ru.mobileup.template.features.utils.integrationTest
import kotlin.time.Duration.Companion.seconds

private val FIRE_TYPE_ID = TestPokemons.fireTypeId.value
private val WATER_TYPE_ID = TestPokemons.waterTypeId.value

class PokemonListComponentTest : FunSpec({

    context("Pokemon list screen") {

        integrationTest("loads the default type pokemon list") {
            // Prepare the default type response
            mockServer.enqueue(
                RequestMatcher.containsPath("type/$FIRE_TYPE_ID"),
                HttpResponse(TestPokemons.firePokemonsJson())
            )
            val component = setupComponent { createPokemonListComponent(it, {}) }

            // Wait for the initial loading to complete
            advanceUntilIdle()

            // Verify the default type list is loaded
            component.pokemonsState.value.loading shouldBe false
            component.pokemonsState.value.data shouldBe TestPokemons.firePokemons
            component.pokemonsState.value.error shouldBe null
        }

        integrationTest("emits pokemon details output when a pokemon is clicked") {
            // Prepare the loaded pokemon list
            mockServer.enqueue(
                RequestMatcher.containsPath("type/$FIRE_TYPE_ID"),
                HttpResponse(TestPokemons.firePokemonsJson())
            )
            val capturer = OutputCapturer<PokemonListComponent.Output>()
            val component = setupComponent { createPokemonListComponent(it, capturer) }
            advanceUntilIdle()

            // Click a pokemon item in the list
            val pokemonId = TestPokemons.detailedPonyta.id
            component.onPokemonClick(pokemonId)

            // Verify the pokemon details output is emitted
            capturer.last shouldBe PokemonListComponent.Output.PokemonDetailsRequested(pokemonId)
        }

        integrationTest("emits dialogs demo output when dialogs demo is clicked") {
            mockServer.enqueue(
                RequestMatcher.containsPath("type/$FIRE_TYPE_ID"),
                HttpResponse(TestPokemons.firePokemonsJson())
            )
            val capturer = OutputCapturer<PokemonListComponent.Output>()
            val component = setupComponent { createPokemonListComponent(it, capturer) }
            advanceUntilIdle()

            component.onDialogsDemoClick()

            capturer.last shouldBe PokemonListComponent.Output.DialogsDemoRequested
        }

        integrationTest("shows loading during refresh") {
            // Prepare initial data and a delayed refresh response
            val firePokemonsJson = TestPokemons.firePokemonsJson()
            mockServer.enqueue(
                RequestMatcher.containsPath("type/$FIRE_TYPE_ID"),
                HttpResponse(firePokemonsJson),
                HttpResponse(
                    firePokemonsJson,
                    delay = 1.seconds
                )
            )
            val component = setupComponent { createPokemonListComponent(it, {}) }
            advanceUntilIdle()

            // Refresh the current list
            component.onRefresh()
            runCurrent()

            // Verify loading is shown during refresh
            component.pokemonsState.value.loading shouldBe true

            // Wait for refresh to complete
            advanceUntilIdle()

            // Verify loading is hidden after refresh
            component.pokemonsState.value.loading shouldBe false
        }

        integrationTest("loads selected type pokemon list when type is clicked") {
            // Prepare responses for default and selected types
            mockServer.enqueue(
                RequestMatcher.containsPath("type/$FIRE_TYPE_ID"),
                HttpResponse(TestPokemons.firePokemonsJson())
            )
            mockServer.enqueue(
                RequestMatcher.containsPath("type/$WATER_TYPE_ID"),
                HttpResponse(TestPokemons.waterPokemonsJson())
            )
            val component = setupComponent { createPokemonListComponent(it, {}) }
            advanceUntilIdle()

            // Select another type and wait for loading
            component.onTypeClick(PokemonType.Water.id)
            advanceUntilIdle()

            // Verify selected type and list are updated
            component.selectedTypeId.value shouldBe PokemonType.Water.id
            component.pokemonsState.value.data shouldBe TestPokemons.waterPokemons
        }

        integrationTest("reloads pokemon list when refresh is requested after error") {
            // Prepare a failed initial response and a successful retry response
            mockServer.enqueue(
                RequestMatcher.containsPath("type/$FIRE_TYPE_ID"),
                HttpResponse(status = HttpStatusCode.NotFound),
                HttpResponse(TestPokemons.firePokemonsJson(), delay = 1.seconds)
            )
            val component = setupComponent { createPokemonListComponent(it, {}) }
            advanceUntilIdle()

            // Refresh loading the current list
            component.onRefresh()
            runCurrent()

            // Verify loading starts again
            component.pokemonsState.value.loading shouldBe true

            // Wait for retry loading to complete
            advanceUntilIdle()

            // Verify the list is loaded after retry
            component.pokemonsState.value.loading shouldBe false
            component.pokemonsState.value.error shouldBe null
            component.pokemonsState.value.data shouldBe TestPokemons.firePokemons
        }
    }
})
