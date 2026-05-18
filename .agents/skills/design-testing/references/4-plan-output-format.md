# Plan Output Format

When asked for a plan, return Kotlin skeletons, not prose checklist text.

## Integration Plan Skeleton (General Scheme)

```kotlin
package <... your package name ...>

import io.kotest.core.spec.style.FunSpec
<... import required testing utilities and matchers ...>

class <Component>Test : FunSpec({

    context("<name of the screen>") {

        integrationTest("description of expected behaviour for first test") {
            // comment for preparation
            <... setup dependencies, mock external behavior and initialize component ...>

            // comment for action
            <... perform user interaction, trigger system event and advance time ...>

            // comment for verification
            <... assert component state, check data changes or verify emitted events ...>
        }

        integrationTest("description of expected behaviour for second test") {
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

        integrationTest("description of expected behaviour for third test") {
            // comment for preparation
            <... setup dependencies, mock external behavior and initialize component ...>

            // comment for action
            <... perform user interaction, trigger system event and advance time ...>

            // comment for verification
            <... assert component state, check data changes or verify emitted events ...>
        }
        ...
    }
})
```

## Unit Plan Skeleton

```kotlin
package <... your package name ...>

import io.kotest.core.spec.style.FunSpec
<... import required matchers and assertions ...>

class <UnitUnderTest>Test : FunSpec({

    context("<name of the logic>") {

        test("description of expected behaviour for first test") {
            // comment for preparation
            <... setup inputs and expected values ...>

            // comment for action
            <... execute isolated logic ...>

            // comment for verification
            <... assert result, boundary mapping, or error ...>
        }
    }
})
```

## Planning Rules

- include only tests you plan to implement;
- usually propose 3-6 tests per file;
- keep intermediate-state integration tests in minority;
- if both test types are needed, output two file skeletons.
- in plan mode for integration tests, keep the exact general scheme above (package, imports, `FunSpec`, one `context`, repeated `integrationTest` blocks).
