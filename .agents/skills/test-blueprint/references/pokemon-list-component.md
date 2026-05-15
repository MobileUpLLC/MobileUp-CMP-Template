# Pokemon List Component Blueprint Example

Use when designing a component blueprint for a list screen that loads data, emits item actions,
handles errors/retry, refreshes, and changes selected type/filter while preserving meaningful
intermediate state.

```text
PokemonListComponentTest (integration)

- loads pokemon list successfully
  🛠️ Prepare a successful pokemon list response
  ▶️ Wait for the initial loading to complete
  ✅ Verify loaded pokemon list state

- emits pokemon details output when a pokemon is clicked
  🛠️ Prepare a successful pokemon list response
  ▶️ Wait for the initial loading to complete
  ▶️ Click a pokemon item in the list
  ✅ Verify the pokemon details output is emitted

- shows error when pokemon list loading fails
  🛠️ Prepare a failed pokemon list response
  ▶️ Wait for the initial loading to complete
  ✅ Verify failed pokemon list state

- shows loading during refresh and updates pokemon list
  🛠️ Prepare initial data and a delayed refresh response
  ▶️ Wait for the initial loading to complete
  ✅ Verify loaded pokemon list state
  ▶️ Refresh pokemon list
  ✅ Verify loading is shown during refresh
  ▶️ Wait for refresh to complete
  ✅ Verify updated pokemon list state

- loads selected type pokemon list while keeping previous data
  🛠️ Prepare successful default and delayed selected pokemon list responses
  ▶️ Wait for the initial loading to complete
  ✅ Verify loaded default pokemon list state
  ▶️ Select another pokemon type
  ✅ Verify selected pokemon type is changed
  ✅ Verify previous pokemon list is kept while selected type is loading
  ▶️ Wait for selected type loading to complete
  ✅ Verify loaded selected pokemon list state

- reloads pokemon list after error
  🛠️ Prepare a failed initial response and a successful retry response
  ▶️ Wait for the initial loading to complete
  ✅ Verify failed pokemon list state
  ▶️ Refresh pokemon list
  ✅ Verify loading starts again
  ▶️ Wait for retry loading to complete
  ✅ Verify loaded pokemon list state after retry
```
