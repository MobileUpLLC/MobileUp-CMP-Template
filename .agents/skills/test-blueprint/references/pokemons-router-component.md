# Pokemons Router Component Blueprint Example

Use when designing a router component blueprint that owns a child stack and coordinates navigation.

```text
PokemonsComponentTest (component)

- shows pokemon list as initial screen
  🛠️ Prepare pokemons flow
  ✅ Verify pokemon list screen is shown initially

- opens pokemon details when pokemon is requested from list
  🛠️ Prepare pokemons flow
  ✅ Verify pokemon list screen is shown
  ▶️ Request pokemon details from the list screen
  ✅ Verify pokemon details screen is shown for requested pokemon
```
