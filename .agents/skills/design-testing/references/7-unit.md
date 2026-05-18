# Unit Test Example

Use this for isolated local behavior with no integration infrastructure.

```kotlin
package <domain package>

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import <project domain imports>

class <UnitUnderTest>Test : FunSpec({

    context("<logic name>") {

        test("maps boundary inputs to expected outputs") {
            // comment for preparation
            val cases = listOf(
                <input A> to <expected A>,
                <input B> to <expected B>
            )

            // comment for action and verification
            cases.forEach { (input, expected) ->
                val actual = <UnitUnderTest>.<function>(input)
                actual shouldBe expected
            }
        }
    }
})
```
