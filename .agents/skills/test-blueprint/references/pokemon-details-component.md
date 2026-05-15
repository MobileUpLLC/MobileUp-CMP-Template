# Pokemon Details Component Blueprint Example

Use when designing a component blueprint for a details screen that loads one entity, supports
error/retry, and can refresh existing data.

```text
PokemonDetailsComponentTest (integration)

- loads pokemon details successfully
  🛠️ Prepare pokemon details data for initial loading
  ✅ Verify initial loading is shown
  ▶️ Wait for the initial loading to complete
  ✅ Verify loaded pokemon details state

- shows error when pokemon details loading fails
  🛠️ Prepare failed pokemon details loading
  ▶️ Wait for the initial loading to complete
  ✅ Verify failed pokemon details state

- reloads pokemon details after error
  🛠️ Prepare failed initial loading and successful retry
  ▶️ Wait for the initial loading to complete
  ✅ Verify failed pokemon details state
  ▶️ Refresh pokemon details
  ✅ Verify loading starts again
  ▶️ Wait for retry loading to complete
  ✅ Verify loaded pokemon details state after retry

- shows loading during refresh and updates pokemon details
  🛠️ Prepare initial and refreshed pokemon details data
  ▶️ Wait for the initial loading to complete
  ✅ Verify loaded pokemon details state
  ▶️ Refresh pokemon details
  ✅ Verify loading is shown during refresh
  ▶️ Wait for refresh to complete
  ✅ Verify updated pokemon details state
```
