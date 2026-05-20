# Pokemon List Fixture

Use this as the source example for feature-local fixtures, mock server enqueue helpers, and list
JSON resources.

```kotlin
package <package_name>.features.pokemons.fixtures

import io.ktor.http.HttpStatusCode
import <package_name>.core_testing.network.HttpResponse
import <package_name>.core_testing.network.MockServer
import <package_name>.core_testing.network.RequestMatcher
import <package_name>.core_testing.network.containsPath
import <package_name>.core_testing.utils.readTestResource
import <package_name>.features.pokemons.domain.Pokemon
import <package_name>.features.pokemons.domain.PokemonId
import <package_name>.features.pokemons.domain.PokemonType
import <package_name>.features.pokemons.domain.PokemonTypeId

data class PokemonListFixture(
    val typeId: PokemonTypeId,
    val resourcePath: String,
    val domain: List<Pokemon>
) {
    companion object {
        val fire = PokemonListFixture(
            typeId = PokemonType.Fire.id,
            resourcePath = "pokemons/fire_pokemons.json",
            domain = listOf(
                Pokemon(id = PokemonId("4"), name = "Charmander"),
                Pokemon(id = PokemonId("77"), name = "Ponyta")
            )
        )

        val fireUpdated = PokemonListFixture(
            typeId = PokemonType.Fire.id,
            resourcePath = "pokemons/fire_pokemons_updated.json",
            domain = listOf(
                Pokemon(id = PokemonId("4"), name = "Charmander"),
                Pokemon(id = PokemonId("77"), name = "Ponyta updated")
            )
        )

        val water = PokemonListFixture(
            typeId = PokemonType.Water.id,
            resourcePath = "pokemons/water_pokemons.json",
            domain = listOf(
                Pokemon(id = PokemonId("7"), name = "Squirtle"),
                Pokemon(id = PokemonId("54"), name = "Psyduck")
            )
        )
    }
}

suspend fun MockServer.enqueuePokemonList(
    fixture: PokemonListFixture,
    isError: Boolean = false
) {
    enqueue(
        matcher = RequestMatcher.containsPath("type/${fixture.typeId.value}"),
        response = if (isError) {
            HttpResponse(status = HttpStatusCode.NotFound)
        } else {
            HttpResponse(readTestResource(fixture.resourcePath))
        }
    )
}
```

`features/src/commonTest/resources/pokemons/fire_pokemons.json`:

```json
{
  "pokemon": [
    {
      "pokemon": {
        "name": "charmander",
        "url": "https://pokeapi.co/api/v2/pokemon/4/"
      },
      "slot": 1
    },
    {
      "pokemon": {
        "name": "ponyta",
        "url": "https://pokeapi.co/api/v2/pokemon/77/"
      },
      "slot": 1
    }
  ]
}
```

`features/src/commonTest/resources/pokemons/fire_pokemons_updated.json`:

```json
{
  "pokemon": [
    {
      "pokemon": {
        "name": "charmander",
        "url": "https://pokeapi.co/api/v2/pokemon/4/"
      },
      "slot": 1
    },
    {
      "pokemon": {
        "name": "ponyta-updated",
        "url": "https://pokeapi.co/api/v2/pokemon/77/"
      },
      "slot": 1
    }
  ]
}
```
