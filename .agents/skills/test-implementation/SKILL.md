---
name: test-implementation
description: Implement provided test blueprints following the project guidelines.
---

# Test Implementation

Use this skill to turn a provided test blueprint into executable Kotlin tests.
Treat the blueprint as the contract for suite name, test type, test names, step order, comments, and step type icons.

## Workflow

1. Inspect the SUT, public APIs, nearby tests, factories, fixtures, and resources.
2. Determine the destination test file and package from the SUT.
3. Add missing fixtures, resources, or helpers only when required by the blueprint.
4. Implement executable code under the preserved blueprint comments.
5. Run validation and report the result.

## Blueprint To Code Mapping

- Header `<SUT>Test (component)` maps to a `FunSpec` class with `componentTest("...")` tests.
- Header `<SUT>Test (unit)` maps to a `FunSpec` class with `test("...")` or `withTests(...)`.
- Bullet names map exactly to `componentTest("...")`, `test("...")`, or table case naming.
- Every blueprint step maps exactly to a Kotlin comment, preserving icon, order, and wording.
- `[table]` maps to Kotest `withTests(...)` with cases from the blueprint.

## File And Package Placement

- Tests live under `features/src/commonTest/kotlin`.
- A test package mirrors the SUT package.
- Feature fixtures live under a feature-local `fixtures` package.
- JSON resources live under `features/src/commonTest/resources/`.

## Unit Tests

- Use `FunSpec` plus `test(...)`.
- Use `withTests(...)` for table cases.
- Use private case data classes when helpful.
- Use `shouldThrow` for exception scenarios.
- Keep the action code under the blueprint action comment.

## Component Tests

### Component Test Scope

- `componentTest(...)` executes with `ComponentTestScope` as the receiver.
- Use the scope for test services, mock server infrastructure, virtual time, and component setup helpers.
- Inspect the current `ComponentTestScope` definition when a test needs capabilities beyond nearby examples.

### Screen Components

- Use `FunSpec` plus `componentTest(...)`.
- Use production DI and repositories with `MockServer`.
- Create the SUT through `setupComponent { createXxxComponent(...) }`.
- Assert only observable state, outputs, or service effects required by the blueprint.

### Router Components

- Use `FunSpec` plus `componentTest(...)`.
- Create the SUT through `setupComponent { createXxxComponent(...) }`.
- Use fake child factories.
- Assert `childStack.activeChild`.
- Trigger child outputs through fake child components.
- Assert parameter passing and navigation decisions owned by project code.

### Time And Loading

- Use `advanceUntilIdle()` for queued async work.
- Use `advanceTimeBy(...)` only when duration matters.
- Check intermediate loading state only when the blueprint includes it.

## Fixtures, Resources, And Mock Server

- Model fixtures as feature-local data classes.
- Keep expected domain values in fixtures when useful.
- Add `MockServer.enqueueXxx(...)` extension helpers beside fixtures.
- Use `RequestMatcher.containsPath(...)`, `HttpResponse(...)`, and `readTestResource(...)`.
- Enqueue responses in scenario consumption order.
- Never invent JSON responses from response DTOs; ask for a real server example or help build a `curl` request.

## Assertions And Test Utilities

- Use Kotest matchers such as `shouldBe`, `shouldNotBeNull`, and `shouldBeInstanceOf`.
- Use `OutputCapturer<T>` for component outputs.
- Prefer observable state and output assertions.
- Check recorded mock server requests only when the request itself is the behavior under test.

## Implementation Style

- Preserve suite name, test type, test names, step comments, icons, and order from the blueprint.
- Add one blank line between implemented logical blocks, matching current `pokemons` tests.
- Keep implementation surgical and local to tests, fixtures, and resources needed by the blueprint.
- Follow nearby test import, ordering, and formatting conventions.

## Guardrails

- Do not invent tests not present in the blueprint.
- Do not change blueprint wording during implementation.
- Do not test private implementation details.
- Do not instantiate `RealXxxComponent` directly.
- Do not refactor production code.

## Validation

- Default command for common test run: `./gradlew :features:testAndroidHostTest`
- Report failed tests and the reason.

## References

Load the closest reference when implementing a similar test:

- [Pokemon power calculator test](references/pokemon-power-calculator-test.md) - unit tests, table cases, and exception assertions.
- [Pokemon list component test](references/pokemon-list-component-test.md) - loading, refresh, retry, output, and selected type behavior.
- [Pokemon details component test](references/pokemon-details-component-test.md) - details loading, error, retry, and refresh behavior.
- [Pokemons router component test](references/pokemons-component-test.md) - router navigation with a fake child factory and the fake factory implementation.
- [Pokemon list fixture](references/pokemon-list-fixture.md) - fixture object, mock server helper, and representative list JSON resources.
- [Pokemon details fixture](references/pokemon-details-fixture.md) - fixture object, mock server helper, and representative details JSON resources.
