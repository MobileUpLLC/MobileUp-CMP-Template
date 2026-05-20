# Pokemon Details Fixture

Use this as the source example for feature-local detail fixtures, mock server enqueue helpers, and
detail JSON resources.

```kotlin
package <package_name>.features.pokemons.fixtures

import io.ktor.http.HttpStatusCode
import <package_name>.core_testing.network.HttpResponse
import <package_name>.core_testing.network.MockServer
import <package_name>.core_testing.network.RequestMatcher
import <package_name>.core_testing.network.containsPath
import <package_name>.core_testing.utils.readTestResource
import <package_name>.features.pokemons.domain.DetailedPokemon
import <package_name>.features.pokemons.domain.PokemonId
import <package_name>.features.pokemons.domain.PokemonType

data class PokemonDetailsFixture(
    val resourcePath: String,
    val domain: DetailedPokemon
) {
    val id get() = domain.id

    companion object {
        val ponyta = PokemonDetailsFixture(
            resourcePath = "pokemons/ponyta_details.json",
            domain = DetailedPokemon(
                id = PokemonId("77"),
                name = "Ponyta",
                height = 1f,
                weight = 30f,
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/77.png",
                types = listOf(PokemonType.Fire)
            )
        )

        val ponytaUpdated = PokemonDetailsFixture(
            resourcePath = "pokemons/ponyta_details_updated.json",
            domain = DetailedPokemon(
                id = PokemonId("77"),
                name = "Ponyta updated",
                height = 1.1f,
                weight = 31f,
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/77.png",
                types = listOf(PokemonType.Fire)
            )
        )
    }
}

suspend fun MockServer.enqueuePokemonDetails(
    fixture: PokemonDetailsFixture,
    isError: Boolean = false
) {
    enqueue(
        matcher = RequestMatcher.containsPath("pokemon/${fixture.id.value}"),
        response = if (isError) {
            HttpResponse(status = HttpStatusCode.NotFound)
        } else {
            HttpResponse(readTestResource(fixture.resourcePath))
        }
    )
}
```

`features/src/commonTest/resources/pokemons/ponyta_details.json`:

```json
{
  "height": 10,
  "id": "77",
  "name": "ponyta",
  "types": [
    {
      "slot": 1,
      "type": {
        "name": "fire",
        "url": "https://pokeapi.co/api/v2/type/10/"
      }
    }
  ],
  "weight": 300
}
```

`features/src/commonTest/resources/pokemons/ponyta_details_updated.json`:

```json
{
  "height": 11,
  "id": "77",
  "name": "ponyta-updated",
  "types": [
    {
      "slot": 1,
      "type": {
        "name": "fire",
        "url": "https://pokeapi.co/api/v2/type/10/"
      }
    }
  ],
  "weight": 310
}
```
