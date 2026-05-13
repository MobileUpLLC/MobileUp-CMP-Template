package ru.mobileup.template.features.pokemons.details

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import ru.mobileup.template.core_testing.network.HttpResponse
import ru.mobileup.template.core_testing.network.RequestMatcher
import ru.mobileup.template.core_testing.network.containsPath
import ru.mobileup.template.features.pokemons.TestPokemons
import ru.mobileup.template.features.pokemons.createPokemonDetailsComponent
import ru.mobileup.template.features.utils.integrationTest
import kotlin.time.Duration.Companion.seconds

private val PONYTA_ID = TestPokemons.ponytaId.value

class PokemonDetailsComponentTest : FunSpec({

    context("Pokemon details screen") {

        integrationTest("loads details successfully") {
            // Prepare a successful details response
            val pokemonId = TestPokemons.ponytaId
            mockServer.enqueue(
                RequestMatcher.containsPath("pokemon/$PONYTA_ID"),
                HttpResponse(TestPokemons.detailedPonytaJson())
            )
            val component = setupComponent { createPokemonDetailsComponent(it, pokemonId) }

            // Wait for the initial loading to complete
            advanceUntilIdle()

            // Verify loaded details are shown
            component.pokemonState.value.data shouldBe TestPokemons.detailedPonyta
            component.pokemonState.value.loading shouldBe false
        }

        integrationTest("shows error when loading fails") {
            // Prepare a failed details response
            val pokemonId = TestPokemons.ponytaId
            mockServer.enqueue(
                RequestMatcher.containsPath("pokemon/$PONYTA_ID"),
                HttpResponse(status = HttpStatusCode.NotFound)
            )
            val component = setupComponent { createPokemonDetailsComponent(it, pokemonId) }

            // Wait for the initial loading to complete
            advanceUntilIdle()

            // Verify error state is shown
            component.pokemonState.value.error.shouldNotBeNull()
        }

        integrationTest("reloads details when refresh is requested after error") {
            // Prepare a failed initial response and a successful retry response
            val pokemonId = TestPokemons.ponytaId
            mockServer.enqueue(
                RequestMatcher.containsPath("pokemon/$PONYTA_ID"),
                HttpResponse(status = HttpStatusCode.NotFound),
                HttpResponse(TestPokemons.detailedPonytaJson(), delay = 1.seconds)
            )
            val component = setupComponent { createPokemonDetailsComponent(it, pokemonId) }
            advanceUntilIdle()

            // Refresh loading details
            component.onRefresh()
            runCurrent()

            // Verify loading starts again
            component.pokemonState.value.loading shouldBe true

            // Wait for retry loading to complete
            advanceUntilIdle()

            // Verify details are loaded after retry
            component.pokemonState.value.loading shouldBe false
            component.pokemonState.value.error shouldBe null
            component.pokemonState.value.data shouldBe TestPokemons.detailedPonyta
        }

        integrationTest("reloads details when refresh is requested") {
            // Prepare initial and refresh responses
            val pokemonId = TestPokemons.ponytaId
            mockServer.enqueue(
                RequestMatcher.containsPath("pokemon/$PONYTA_ID"),
                HttpResponse(TestPokemons.detailedPonytaJson()),
                HttpResponse(TestPokemons.detailedPonytaUpdatedJson())
            )
            val component = setupComponent { createPokemonDetailsComponent(it, pokemonId) }
            advanceUntilIdle()

            // Refresh loaded details
            component.onRefresh()
            advanceUntilIdle()

            // Verify updated details are shown
            component.pokemonState.value.loading shouldBe false
            component.pokemonState.value.error shouldBe null
            component.pokemonState.value.data shouldBe TestPokemons.detailedPonytaUpdated
        }
    }
})
