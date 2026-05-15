# Pokemons Router Component Blueprint Example

Use when designing a router component blueprint that owns a child stack and coordinates navigation.

```text
PokemonsComponentTest (integration)

- shows pokemon list as initial screen
  🛠️ Prepare pokemon list data for initial screen
  ✅ Verify pokemon list screen is shown initially

- opens pokemon details when pokemon is requested from list
  🛠️ Prepare pokemon list and details data
  ▶️ Wait for the initial loading to complete
  ✅ Verify pokemon list screen is active
  ▶️ Request pokemon details from the list
  ✅ Verify pokemon details screen is active
```
