# Pokemons Router Component Blueprint Example

Use when designing a router component blueprint that owns a child stack and coordinates navigation.

```text
PokemonsComponentTest (integration)

- shows pokemon list as initial screen
  🛠️ Prepare initial pokemon list response
  ✅ Verify pokemon list screen is shown initially

- opens pokemon details when pokemon is requested from list
  🛠️ Prepare pokemon list and details responses
  ▶️ Wait for the initial loading to complete
  ✅ Verify pokemon list screen is active
  ▶️ Request pokemon details from the list
  ✅ Verify pokemon details screen is active
  ▶️ Wait for pokemon details loading to complete
  ✅ Verify loaded pokemon details state

- returns to pokemon list when back is requested from details
  🛠️ Prepare pokemon list response
  ▶️ Wait for the initial loading to complete
  ✅ Verify pokemon list screen is active
  ▶️ Request pokemon details from the list
  ✅ Verify pokemon details screen is active
  ▶️ Go back from pokemon details
  ✅ Verify pokemon list screen is active

- does not duplicate pokemon details when same pokemon is requested twice
  🛠️ Prepare pokemon list and details response
  ▶️ Wait for the initial loading to complete
  ✅ Verify pokemon list screen is active
  ▶️ Request pokemon details from the list
  ✅ Verify pokemon details screen is active
  ▶️ Request the same pokemon details from the list again
  ✅ Verify pokemon details screen is still active
  ✅ Verify navigation stack has one pokemon details screen
```
