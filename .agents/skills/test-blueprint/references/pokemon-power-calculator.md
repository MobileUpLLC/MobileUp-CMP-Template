# Pokemon Power Calculator Blueprint Example

Use when designing a unit blueprint for a pure function with repeated input variants and explicit
validation/error cases.

```text
PokemonPowerCalculatorTest (unit)

- calculates score for pokemon stats [table]
  cases: Pikachu, Charizard, Mewtwo
  🛠️ Prepare pokemon stats
  ▶️ Calculate pokemon power score
  ✅ Verify calculated pokemon power score

- throws on invalid stats below range
  🛠️ Prepare stats with hp below allowed range
  ▶️ Calculate pokemon power score
  ✅ Verify validation message for the invalid stat

- throws on invalid stats above range
  🛠️ Prepare stats with speed above allowed range
  ▶️ Calculate pokemon power score
  ✅ Verify validation message for the invalid stat
```
