# Testing Helpers

This reference lists helper classes from `core-testing` and their intended usage.

## Helper Table

| Helper | What it provides | When to use |
|---|---|---|
| `IntegrationTestScope` | `mockServer`, `testMessageService`, virtual-time controls, component setup helpers | Inside `integrationTest(...)` blocks |
| `IntegrationTestScopeImpl` | Bridge between Kotest scope, Koin graph, and test scheduler | Infrastructure wiring (usually in shared DSL, not in test body) |
| `TestComponentContext` | Controllable Decompose lifecycle context | Manual lifecycle transitions in tests |
| `MockServer` | In-memory response queue + recorded requests | Deterministic network behavior; request-shape assertions |
| `RequestMatcher` | Matching DSL (`containsPath`, `exactPath`, `method`) | Response selection for specific requests |
| `HttpResponse` | Mock response model (`status`, `body`, `headers`, `delay`) | Success/error/delayed network scenarios |
| `HttpRequest` | Recorded request model + `queryParam(...)` helper | Assertions on outgoing query/body/headers when behavior requires it |
| `createMockHttpEngine` | Ktor engine backed by `MockServer` | Test DI network setup |
| `TestNetworkConnectivityProvider` | Mutable connectivity state | Online/offline behavior checks |
| `OutputCapturer<T>` | Output history (`first`, `last`, `all`, `isEmpty`) | Component `Output` assertions without mock frameworks |
| `TestMessageService` | Message history + stream | Message side-effect assertions |

## Virtual Time Usage

| Operation | Typical usage |
|---|---|
| `runCurrent()` | Assert immediate intermediate state after triggering action |
| `advanceUntilIdle()` | Assert final state after async processing completes |
| `advanceTimeBy(...)` | Assert behavior at exact debounce/timeout/delay checkpoints |

## Usage Constraints

1. Prefer helper usage over ad-hoc scaffolding.
2. Prefer state/output/message assertions over request assertions unless request shape is the behavior.
3. Keep infrastructure setup in shared test DSL/modules, not in individual test bodies.
