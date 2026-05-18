# Test Structure

This reference defines required structure, style, and naming for both integration and unit tests.

## Style Baseline

- Use `FunSpec` from Kotest.
- Usually keep one `context("<screen or logic name>")` block per file.
- One test should assert one observable behavior.
- Test names are short English behavior sentences.
- Comments in test body are short English scenario-step comments.
- Do not use `Arrange`, `Act`, `Assert` labels.
- Keep one empty line between logical steps.

## Naming Rules

Good naming:

- describes user-visible behavior;
- avoids implementation wording;
- uses neutral style without `should` prefix.

Good examples:

- `loads the default data set`
- `shows loading during refresh`
- `emits details output when item is clicked`

Bad examples:

- `should call repository twice`
- `test refresh logic`
- `checks state and output`

## Integration: Main Variant (Default)

Most integration tests should use this structure:

```kotlin
integrationTest("<description of the expected behavior>") {
    // comment for preparation
    <... setup dependencies, mock external behavior and initialize component ...>

    // comment for action
    <... perform user interaction, trigger system event and advance time ...>

    // comment for verification
    <... assert component state, check data changes or verify emitted events ...>
}
```

## Integration: Intermediate-State Variant (Rare)

Use this only when intermediate behavior is product-significant:

```kotlin
integrationTest("<description of the expected behavior>") {
    // comment for preparation
    <... setup dependencies, mock external behavior and initialize component ...>

    // comment for action
    <... perform user interaction or trigger system event ...>

    // comment for intermediate verification
    <... assert intermediate state (e.g. loading is active, previous data is retained) ...>

    // comment for waiting
    <... advance time to let asynchronous operations complete ...>

    // comment for final verification
    <... assert final state, check updated data or verify emitted events ...>
}
```

## Unit Test Structure

For isolated local behavior, use plain `test(...)`:

```kotlin
test("<description of the expected behavior>") {
    // comment for preparation
    <... setup inputs and expectations ...>

    // comment for action
    <... execute function or local logic ...>

    // comment for verification
    <... assert result, boundary mapping, or error ...>
}
```

## Integration Context Contract

In this template, integration tests use existing `integrationTest(...)` DSL and `IntegrationTestScope` helpers.
Do not re-create this infrastructure manually inside each test.
