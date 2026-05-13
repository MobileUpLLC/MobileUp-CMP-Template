package ru.mobileup.template.core_testing.scope

import com.arkivanov.essenty.lifecycle.Lifecycle
import io.kotest.core.test.TestScope
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.core_testing.message.data.TestMessageService
import ru.mobileup.template.core_testing.network.MockServer
import ru.mobileup.template.core_testing.utils.TestComponentContext
import kotlin.time.Duration

/**
 * Test DSL context used inside integration tests.
 *
 * Provides access to mock infrastructure, virtual time controls, and component setup helpers.
 */
interface IntegrationTestScope : TestScope {
    val mockServer: MockServer
    val testMessageService: TestMessageService

    /**
     * Advances virtual time until no pending tasks remain.
     */
    fun advanceUntilIdle()

    /**
     * Advances virtual time by [delayTime].
     */
    fun advanceTimeBy(delayTime: Duration)

    /**
     * Executes tasks scheduled for the current virtual time moment.
     */
    fun runCurrent()

    /**
     * Creates a component and moves lifecycle to [targetState].
     */
    fun <T> setupComponent(
        targetState: Lifecycle.State = Lifecycle.State.RESUMED,
        create: ComponentFactory.(TestComponentContext) -> T
    ): T

    /**
     * Same as [setupComponent], but also returns the created [TestComponentContext]
     * for manual lifecycle manipulations in a test.
     */
    fun <T> setupComponentWithContext(
        targetState: Lifecycle.State = Lifecycle.State.RESUMED,
        create: ComponentFactory.(TestComponentContext) -> T
    ): Pair<T, TestComponentContext>
}
