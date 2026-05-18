# Integration Intermediate-State Example

Use this when you need to verify temporary loading state before final state.

```kotlin
package <feature package>

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds
import <project integration test imports>

class <ComponentName>Test : FunSpec({

    context("<screen name>") {

        integrationTest("shows loading during refresh and then displays updated data") {
            // comment for preparation
            mockServer.enqueue(
                RequestMatcher.containsPath("resource/<id>"),
                HttpResponse(<initial body>),
                HttpResponse(<updated body>, delay = 1.seconds)
            )
            val component = setupComponent { create<ComponentName>(it, <params>) }
            advanceUntilIdle()

            // comment for action
            component.onRefresh()
            runCurrent()

            // comment for intermediate verification
            component.<state>.value.loading shouldBe true

            // comment for waiting
            advanceUntilIdle()

            // comment for final verification
            component.<state>.value.loading shouldBe false
            component.<state>.value.data shouldBe <updated expected data>
        }
    }
})
```
