package ru.mobileup.template.features.pokemons.presentation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import ru.mobileup.template.core.utils.activeChild
import ru.mobileup.template.features.pokemons.createPokemonsComponent
import ru.mobileup.template.features.pokemons.domain.PokemonId
import ru.mobileup.template.features.pokemons.presentation.list.PokemonListComponent
import ru.mobileup.template.features.utils.componentTest

class PokemonsComponentTest : FunSpec({

    componentTest("shows pokemon list as initial screen") {
        // 🛠️ Prepare pokemons flow
        val childComponentFactory = TestPokemonsChildComponentFactory()
        val component = setupComponent {
            createPokemonsComponent(it, childComponentFactory)
        }

        // ✅ Verify pokemon list screen is shown initially
        component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.List>()
    }

    componentTest("opens pokemon details when pokemon is requested from list") {
        // 🛠️ Prepare pokemons flow
        val childComponentFactory = TestPokemonsChildComponentFactory()
        val component = setupComponent {
            createPokemonsComponent(it, childComponentFactory)
        }

        // ✅ Verify pokemon list screen is shown
        component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.List>()

        // ▶️ Request pokemon details from the list screen
        val pokemonId = PokemonId("77")
        childComponentFactory.listOutput.emit(
            PokemonListComponent.Output.PokemonDetailsRequested(pokemonId)
        )

        // ✅ Verify pokemon details screen is shown for requested pokemon
        val detailsChild =
            component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.Details>()
        detailsChild.component.pokemonId shouldBe pokemonId
    }
})
