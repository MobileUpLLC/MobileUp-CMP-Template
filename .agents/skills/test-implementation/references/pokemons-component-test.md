# Pokemons Component Test

Use this as the source example for router tests that use a fake child factory and trigger child
outputs through fake children.

`PokemonsComponentTest.kt`:

```kotlin
package <package_name>.features.pokemons.presentation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import <package_name>.core.utils.activeChild
import <package_name>.features.pokemons.createPokemonsComponent
import <package_name>.features.pokemons.domain.PokemonId
import <package_name>.features.pokemons.presentation.list.FakePokemonListComponent
import <package_name>.features.pokemons.presentation.list.PokemonListComponent
import <package_name>.features.utils.componentTest

class PokemonsComponentTest : FunSpec({

    componentTest("shows pokemon list as initial screen") {
        // 🛠️ Prepare pokemons flow
        val childComponentFactory = FakePokemonsChildComponentFactory()
        val component = setupComponent {
            createPokemonsComponent(it, childComponentFactory)
        }

        // ✅ Verify pokemon list screen is shown initially
        component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.List>()
    }

    componentTest("opens pokemon details when pokemon is requested from list") {
        // 🛠️ Prepare pokemons flow
        val childComponentFactory = FakePokemonsChildComponentFactory()
        val component = setupComponent {
            createPokemonsComponent(it, childComponentFactory)
        }

        // ✅ Verify pokemon list screen is shown
        val listChild =
            component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.List>()

        // ▶️ Request pokemon details from the list screen
        val pokemonId = PokemonId("77")
        val listComponent = listChild.component.shouldBeInstanceOf<FakePokemonListComponent>()
        listComponent.emitOutput(
            PokemonListComponent.Output.PokemonDetailsRequested(pokemonId)
        )

        // ✅ Verify pokemon details screen is shown for requested pokemon
        val detailsChild =
            component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.Details>()
        detailsChild.component.pokemonId shouldBe pokemonId
    }
})
```

`FakePokemonsChildComponentFactory.kt`:

```kotlin
package <package_name>.features.pokemons.presentation

import com.arkivanov.decompose.ComponentContext
import <package_name>.features.pokemons.domain.PokemonId
import <package_name>.features.pokemons.presentation.details.FakePokemonDetailsComponent
import <package_name>.features.pokemons.presentation.details.PokemonDetailsComponent
import <package_name>.features.pokemons.presentation.list.FakePokemonListComponent
import <package_name>.features.pokemons.presentation.list.PokemonListComponent

class FakePokemonsChildComponentFactory : PokemonsChildComponentFactory {

    override fun createPokemonListComponent(
        componentContext: ComponentContext,
        onOutput: (PokemonListComponent.Output) -> Unit
    ): PokemonListComponent {
        return FakePokemonListComponent(onOutput)
    }

    override fun createPokemonDetailsComponent(
        componentContext: ComponentContext,
        pokemonId: PokemonId
    ): PokemonDetailsComponent {
        return FakePokemonDetailsComponent(pokemonId)
    }
}
```
