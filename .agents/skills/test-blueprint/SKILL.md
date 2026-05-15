---
name: test-blueprint
description: Create compact, reviewable high-level designs for test suites before implementation or structural review.
---

# Test Blueprint

## Purpose

A **Test Blueprint** is a compact, human-reviewable design of a test suite. It describes the test
suite type, test names, table cases, and scenario steps before writing executable tests.

A **Test Implementation** is the executable test code created from the blueprint.

This skill produces blueprint text only. Do not implement tests, edit fixtures, change production
code, or run validation as part of this skill unless the user explicitly asks for implementation
work in a later request.

## Workflow

1. Inspect the SUT and nearby tests before drafting.
2. Choose the test type: integration or unit.
3. Produce the canonical blueprint format.
4. Keep the output concise enough for human review and implementation handoff.

## Choosing Test Type

Use **integration** for component-level behavior through DI, repository, mock network, parsing,
state, outputs, or navigation.

Use **unit** for pure functions, calculations, validation, and deterministic domain logic.

## Blueprint Format

Use this shape:

```text
<SUT>Test (integration)

- observable behavior name
  🛠️ Prepare data or test subject
  ▶️ Perform action or wait for progress
  ✅ Verify observable result
```

Rules:

- Header is `<SUT>Test (integration)` or `<SUT>Test (unit)`.
- Test names are bullet items.
- Test names describe observable behavior in short present-simple English.
- Do not use `should` in test names.
- Put one blank line between tests.
- Step lines start with `🛠️`, `▶️`, or `✅`.
- Table tests are marked with `[table]` after the test name and followed by a compact cases line:
  `cases: Pikachu, Charizard, Mewtwo`.
- Do not include source code, imports, exact fixtures, exact assertions, or mock setup details.

## Step Definitions

- `🛠️` means setup/preparation: input data, fixtures, mock responses, initial state, test subject
  creation, capturers, or fakes.
- `▶️` means action/progression: user actions, public method calls, navigation requests, lifecycle
  changes, waiting for async work, or function calls under test.
- `✅` means verification: observable state, output events, navigation stack, error state/message,
  final calculated result, preserved previous data, or observable external calls.
- Waiting must be explicit as a `▶️` step, for example: `▶️ Wait for the initial loading to complete`.
- Integration blueprints must list required mocked responses in `🛠️` steps. Name response kinds, 
  not exact fixture names, JSON files, or matcher details. Mock servers fail on unmocked requests, 
  so router tests may need child-screen responses even when the blueprint only verifies navigation.
- Verify calls only when the call itself is observable behavior, such as sending data to backend or
  opening an external app.

## Coverage

### Coverage Principles

- Keep one test focused on one behavior.
- Multi-step temporal flows are allowed when they describe one behavior over time.
- Treat the coverage lists below as prompts, not as mandatory checklists.
- Avoid exhaustive permutations and low-value tests that do not increase confidence.

### Regular Component Coverage

- Consider initial success state, initial error state, retry or refresh recovery, each public user
  action exposed by the component, parameter changes such as query/filter/id/type/tab, and side
  effects such as output events, messages, dialogs, or navigation requests.
- Include intermediate-state checks only when the temporary state is user-significant, protects
  previous content from disappearing, prevents duplicate actions, or covers known regression risk.

### Router Component Coverage

- Consider initial child screen, forward navigation, back or close navigation, child output
  handling, branch transitions for different child outputs, and duplicate-safe navigation.
- Keep router tests focused on navigation. Include data-loading assertions only when loaded data is
  part of the behavior being proved.

### Unit Coverage

- Consider representative happy path, meaningful boundary behavior, invalid input or error path.
- Use table tests only for unit tests where the same rule is checked against multiple input
  variants.

### When Not to Add a Separate Test

- Do not add a test when it only changes constants for an already covered behavior.
- Do not test private implementation details or internal calls.
- Do not add isolated DTO/domain mapping unit tests when integration tests already cover parsing
  through mocked responses and observed state.
- Do not split steps of one coherent user scenario into separate tests unless each step is
  independently meaningful.

## References

Load the closest reference when it helps:

- [Pokemon details component](references/pokemon-details-component.md) — details loading component blueprint.
- [Pokemon list component](references/pokemon-list-component.md) — list loading component blueprint, 
  including type/filter changing behavior.
- [Pokemons router component](references/pokemons-router-component.md) — router blueprint.
- [Pokemon power calculator](references/pokemon-power-calculator.md) — unit blueprint for a pure function with table cases.
