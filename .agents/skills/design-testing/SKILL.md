---
name: design-testing
description: Design and implement unit and integration tests for Kotlin features (new or existing), including cases where tests are requested together with feature implementation; choose test level by behavior, apply consistent Kotest structure, and use existing test helpers
---

# Design Testing

Primary rules for test design and implementation. Read this file fully before opening references.

## Scope

Use this skill to:

- choose test type (`test` vs `integrationTest`) by behavior;
- define minimal scenario sets;
- generate test plans as Kotlin skeletons;
- implement tests with consistent structure, naming, and helper usage.

## Core Workflow

1. Split requested behavior into observable behaviors.
2. Classify each behavior as isolated (unit) or cross-layer (integration).
3. Group behaviors into target test files.
4. Select minimal scenario sets.
5. Implement tests using required structure and existing helpers.

## Test Style (Mandatory)

- Use `FunSpec`.
- Usually keep one `context("<screen or logic name>")` per file.
- Do not use base spec classes (`BaseSpec`-style inheritance).
- One test = one observable behavior.
- Test names are short English behavior sentences.
- Test-body comments are short English scenario-step comments.
- Do not use `Arrange`, `Act`, `Assert` labels.
- Separate logical blocks with exactly one empty line.

Naming examples:

- good: `loads the default list`, `shows loading during refresh`, `emits details output when item is clicked`
- bad: `should call repository twice`, `test refresh logic`, `checks state and output and retry`

## Test Type Selection

Detailed rules: [Test Type Selection](references/2-test-type-selection.md).

Use `integrationTest(...)` for cross-layer behavior that depends on component lifecycle, DI setup, repository/parsing/network path, async loading flow, or navigation/output coupled with loaded data.

Use `test(...)` for isolated local logic (calculations, mapping, validation, deterministic local transitions) that is meaningful without component lifecycle/DI/network.

Integration-level chain in this template:

`Component -> Repository -> Network mock -> Parsing -> State/Output`

## Integration Test Structure (Mandatory)

Main variant (default, majority):

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

Intermediate-state variant (rare):

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

## Minimal Scenario Set

Detailed checklists: [Component Scenario Selection](references/3-component-scenario-selection.md).

Heuristic ranges:

- integration, simple screen: 3-5 tests;
- integration, richer screen with filters/retry/refresh/side effects: 4-7 tests;
- integration, router: 2-5 tests;
- unit, isolated logic: 2-4 focused tests.

## Helper Summary

Detailed helper reference: [Testing Helpers](references/6-testing-helpers.md).

| Tool | Use when | Key behavior |
|---|---|---|
| `StandardTestDispatcher(testScheduler)` | Base dispatcher for integration tests | Queues coroutines; test controls execution timing |
| `runCurrent()` | Need immediate tasks at current virtual time | Best for intermediate-state assertions |
| `advanceUntilIdle()` | Need final stable state after async work | Runs all scheduled tasks to completion |
| `advanceTimeBy(delayTime)` | Need exact virtual-time checkpoint | Moves virtual clock by precise duration |
| `setupComponent(...)` | Need component, no manual lifecycle changes | Creates component and moves to target lifecycle state |
| `setupComponentWithContext(...)` | Need manual lifecycle transitions | Returns `component + TestComponentContext` |
| `MockServer` | Need deterministic network behavior | One-time response queue + request recording |
| `RequestMatcher` | Need request-specific mock matching | Path/method DSL (`containsPath`, `exactPath`, `method`) |
| `HttpResponse` | Need success/error/delayed response mocking | Supports `status`, `body`, `headers`, `delay` |
| `OutputCapturer<T>` | Need output event assertions | `first`, `last`, `all`, `isEmpty` |
| `TestMessageService` | Need message side-effect assertions | Message history + last message |
| `TestNetworkConnectivityProvider` | Need connectivity-dependent behavior checks | Deterministic online/offline switching |

## Plan Output

Use Kotlin skeletons only. Do not output prose-only plans.

Format contract: [Plan Output Format](references/4-plan-output-format.md).
In plan mode, integration-test plans must follow the exact general scheme from that reference:
`package` -> imports -> `FunSpec` -> one `context` -> repeated `integrationTest` blocks
(including the default 3-block variant, rare intermediate-state variant, and additional standard variant).

If both types are needed, output two files:

1. unit test skeleton;
2. integration test skeleton.

## Examples

> **Important:** Open examples only when specific details are needed. Do not load all examples at once.

- [Integration Final Verification](references/5-integration-final.md)
- [Integration Intermediate State](references/6-integration-intermediate.md)
- [Unit Test](references/7-unit.md)
- [Integration Multi-Scenario File](references/8-integration-multi-scenarios.md)

## Implementation Rules

- Keep test code comments in English.
- Reuse existing DSL/helpers from the project.
- Do not add new abstractions unless existing helpers are insufficient.
- If implementation diverges from planned scenario split, explain why briefly.
