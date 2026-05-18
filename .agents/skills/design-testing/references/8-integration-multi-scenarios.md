# Integration Multi-Scenario File Example

Use this as a compact template for a component test file with several integration scenarios.

```kotlin
package <feature package>

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import <project integration test imports>

class <ComponentName>Test : FunSpec({

    context("<screen name>") {

        integrationTest("loads details successfully") {
            // comment for preparation
            mockServer.enqueue(
                RequestMatcher.containsPath("resource/<id>"),
                HttpResponse(<successful response body>)
            )
            val component = setupComponent { create<ComponentName>(it, <id>) }

            // comment for action
            advanceUntilIdle()

            // comment for verification
            component.<state>.value.data shouldBe <expected entity>
            component.<state>.value.loading shouldBe false
        }

        integrationTest("shows error when loading fails") {
            // comment for preparation
            mockServer.enqueue(
                RequestMatcher.containsPath("resource/<id>"),
                HttpResponse(status = <error status>)
            )
            val component = setupComponent { create<ComponentName>(it, <id>) }

            // comment for action
            advanceUntilIdle()

            // comment for verification
            component.<state>.value.error.shouldNotBeNull()
        }
    }
})
```
