---
name: test-blueprint
description: Create compact, reviewable high-level designs for test suites before implementation or structural review.
---

# Test Blueprint

## Purpose

A **Test Blueprint** is a compact, human-reviewable design of a test suite. It describes the SUT name, 
the test suite type, test names, table cases, and scenario steps before writing executable tests.

A **Test Implementation** is the executable test code created from the blueprint.

This skill produces blueprint text only. Do not implement tests, edit fixtures, change production
code, or run validation as part of this skill.

## Workflow

1. Inspect the SUT and nearby tests before drafting.
2. Choose the test type: component or unit.
3. Produce the canonical blueprint format.

## Choosing Test Type

Use **component** for Decompose component behavior: screen state, outputs, data loading through DI
and mock network, router navigation, root navigation, parameter passing, messages, and dialogs.

Use **unit** for non-trivial domain logic: algorithms, calculations, validation rules, state
reducers, branching/combinatorics.

## Blueprint Format

Use this shape:

```text
<SUT>Test (component)

- observable behavior name
  🛠️ Prepare scenario data or test subject
  ▶️ Perform action or wait for progress
  ✅ Verify observable result
```

Rules:

- Header is `<SUT>Test (component)` or `<SUT>Test (unit)`.
- Test names are bullet items.
- Test names describe observable behavior in short present-simple English.
- Do not use `should` in test names.
- Put one blank line between tests.
- Step lines start with `🛠️`, `▶️`, or `✅`.
- Table tests are marked with `[table]` after the test name and followed by a compact cases line:
  `cases: Pikachu, Charizard, Mewtwo`.
- Do not include source code, imports, exact fixtures, exact assertions, or mock setup details.

## Step Definitions

- `🛠️` means setup: scenario data, input data, initial state, test subject creation,
  capturers, or fakes.
- `▶️` means action: user actions, public method calls, navigation requests, lifecycle
  changes, waiting for async work, or function calls under test.
- `✅` means verification: observable state, output events, navigation stack, error state/message,
  final calculated result, preserved previous data, or observable external calls.
- Waiting must be explicit as a `▶️` step, for example: `▶️ Wait for the initial loading to complete`.
- Component blueprints must describe required prepared data, loading outcomes, or flow setup
  in `🛠️` steps. Name scenario conditions, not exact fixture names, JSON files, matcher details,
  mock server mechanics, or child factory implementation details.
- Verify external calls only when the call is the behavior under test, for example 
  submitting a form to the backend or logging an analytics event.

## Coverage

### Coverage Principles

- Keep one test focused on one behavior.
- Multi-step temporal flows are allowed for component tests when they describe one behavior over time.
- Avoid exhaustive permutations and low-value tests that do not increase confidence.
- Each tested Decompose component gets its own <ComponentName>Test blueprint.

### Regular Decompose Component Coverage

- Consider initial success state, initial error state, retry or refresh recovery, each public user
  action exposed by the component, parameter changes such as query/filter/id/type/tab, and side
  effects such as output events, messages, dialogs, or navigation requests.
- Include intermediate-state checks only when the temporary state is user-significant or covers known regression risk.

### Router Decompose Component Coverage

For routers:

- Always cover the initial child as the component smoke test.
- Add other router tests only for routing decisions owned by project code: child output to
  screen/config mapping, parameter passing, conditional navigation, and custom back or close
  behavior.
- Do not test standard Decompose mechanics in every router: `handleBackButton`, simple `pop`,
  or duplicate safety.
- Keep router tests focused on navigation. Prefer test child factories with fake children over
  real child components. Initiate child outputs through the active fake child from the router stack.

### Unit Test Coverage

- Add unit tests when the SUT has meaningful branches, boundary conditions, invalid inputs,
  combinatorial cases, or business rules that can be checked without DI, components, or network.
- Use table tests only for unit tests where the same rule is checked against multiple input
  variants.
- Do not hide non-trivial algorithms or combinatorics inside component tests. If logic
  has many branches or cases, design a separate unit blueprint for that logic.

### When Not to Add a Separate Test

- Do not add equivalent-value cases unless they cover a new branch, boundary, rule, or observable behavior.
- Do not test private implementation details or internal calls.
- Do not add unit tests for trivial DTO/entity mappers, one-to-one field copies, enum/name
  conversions, thin formatters, or pass-through wrappers.
- Do not add a unit test when the behavior is already covered by a component test
  through observable state or output and the unit test would not add confidence.
- Do not split steps of one coherent user scenario into separate tests unless each step is
  independently meaningful.

## References

Load the closest reference when it helps:

- [Pokemon details component](references/pokemon-details-component.md) — details loading component blueprint.
- [Pokemon list component](references/pokemon-list-component.md) — list loading component blueprint, including type/filter changing behavior.
- [Pokemons router component](references/pokemons-router-component.md) — router blueprint.
- [Pokemon power calculator](references/pokemon-power-calculator.md) — unit blueprint for a pure function with table cases.
