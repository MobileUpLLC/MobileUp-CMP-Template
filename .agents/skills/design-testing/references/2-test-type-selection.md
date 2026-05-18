# Test Type Selection

Choose test type per behavior, not per class and not per method.

## Core Rule

Pick the lightest test type that still proves behavior reliably.

- Use integration tests for broad cross-layer behavior.
- Use unit tests for isolated local behavior.

## Choose Integration Test When

Use `integrationTest(...)` if behavior requires at least one of these:

- component lifecycle and DI setup;
- repository wiring and parsing path;
- network mock interaction with state/output verification;
- refresh/retry/filter scenarios tied to asynchronous loading;
- navigation/output behavior coupled with loaded data.

Mental check:
If behavior mentions both screen logic and data-loading flow in one sentence, it is integration-level.

## Choose Unit Test When

Use `test(...)` if behavior is meaningful without DI and network stack:

- pure calculations;
- local mapping and validation rules;
- boundary behavior for deterministic logic;
- error handling of local pure functions;
- deterministic state transition of isolated logic.

Mental check:
If behavior can be explained without component environment, it is unit-level.

## Anti-Patterns

DO NOT CHOOSE integration tests only because:

- code location is inside a feature/component package;
- integration infrastructure already exists;
- method calls repository indirectly.

DO NOT CHOOSE unit tests when:

- cross-layer behavior is the actual acceptance criteria;
- local mocks would hide the behavior being validated.

## Decision Output

Before planning tests, write an internal split:

- isolated behaviors -> unit test file;
- cross-layer behaviors -> integration test file.
