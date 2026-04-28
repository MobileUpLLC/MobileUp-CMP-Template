package ru.mobileup.template.features.pokemons

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import ru.mobileup.template.core_testing.network.HttpResponse
import ru.mobileup.template.core_testing.network.RequestMatcher
import ru.mobileup.template.core_testing.network.containsPath
import ru.mobileup.template.features.pokemons.presentation.PokemonsComponent
import ru.mobileup.template.features.utils.integrationTest

private val FIRE_TYPE_ID = TestPokemons.fireTypeId.value
private val CHARMANDER_ID = TestPokemons.charmanderId.value

class PokemonsComponentTest : FunSpec({

    context("Pokemons сomponent") {

        integrationTest("shows list as initial screen") {
            val component = setupComponent { createPokemonsComponent(it) }

            // Verify list screen is shown initially
            component.childStack.value.active.instance.shouldBeInstanceOf<PokemonsComponent.Child.List>()
        }

        integrationTest("navigates to details when pokemon is clicked in the list") {
            // Prepare list and details responses
            mockServer.enqueue(
                RequestMatcher.containsPath("type/$FIRE_TYPE_ID"),
                HttpResponse(TestPokemons.firePokemonsJson())
            )
            mockServer.enqueue(
                RequestMatcher.containsPath("pokemon/$CHARMANDER_ID"),
                HttpResponse(TestPokemons.detailedPonytaJson())
            )
            val component = setupComponent { createPokemonsComponent(it) }
            advanceUntilIdle()

            // Click a pokemon in the list and deliver router events
            val listChild =
                component.childStack.value.active.instance as PokemonsComponent.Child.List
            listChild.component.onPokemonClick(TestPokemons.firePokemons.first().id)
            runCurrent()

            // Verify details screen is shown
            component.childStack.value.active.instance.shouldBeInstanceOf<PokemonsComponent.Child.Details>()
        }

        integrationTest("navigates to dialogs demo when dialogs demo is clicked in the list") {
            mockServer.enqueue(
                RequestMatcher.containsPath("type/$FIRE_TYPE_ID"),
                HttpResponse(TestPokemons.firePokemonsJson())
            )
            val component = setupComponent { createPokemonsComponent(it) }
            advanceUntilIdle()

            val listChild =
                component.childStack.value.active.instance as PokemonsComponent.Child.List
            listChild.component.onDialogsDemoClick()
            runCurrent()

            component.childStack.value.active.instance.shouldBeInstanceOf<PokemonsComponent.Child.DialogsDemo>()
        }
    }
})
