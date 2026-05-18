# Integration Final Verification Example

Use this when you need a canonical integration test with the default three-block flow.

```kotlin
package <feature package>

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import <project integration test imports>

class <ComponentName>Test : FunSpec({

    context("<screen name>") {

        integrationTest("loads the default data set") {
            // comment for preparation
            mockServer.enqueue(
                RequestMatcher.containsPath("resource/<default-id>"),
                HttpResponse(<successful response body>)
            )
            val component = setupComponent { create<ComponentName>(it, <params>) }

            // comment for action
            advanceUntilIdle()

            // comment for verification
            component.<state>.value.loading shouldBe false
            component.<state>.value.data shouldBe <expected data>
            component.<state>.value.error shouldBe null
        }
    }
})
```
