package ru.mobileup.template.core_testing.component_test

import com.arkivanov.essenty.lifecycle.Lifecycle
import io.kotest.core.test.TestScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.core.settings.SettingsFactory
import ru.mobileup.template.core_testing.network.MockServer
import ru.mobileup.template.core_testing.network.TestNetworkConnectivityProvider
import ru.mobileup.template.core_testing.test_services.TestExternalAppService
import ru.mobileup.template.core_testing.test_services.TestLocationService
import ru.mobileup.template.core_testing.test_services.TestMessageService
import ru.mobileup.template.core_testing.test_services.TestPermissionService
import kotlin.time.Duration

/**
 * Test DSL context used inside component tests.
 *
 * Provides access to mock infrastructure, virtual time controls, and component setup helpers.
 */
interface ComponentTestScope : TestScope {
    val mockServer: MockServer
    val messageService: TestMessageService
    val permissionService: TestPermissionService
    val locationService: TestLocationService
    val externalAppService: TestExternalAppService
    val settingsFactory: SettingsFactory
    val networkConnectivityProvider: TestNetworkConnectivityProvider

    /**
     * Runs already scheduled tasks at the current virtual time.
     */
    fun runCurrent()

    /**
     * Advances virtual time until no pending tasks remain.
     */
    fun advanceUntilIdle()

    /**
     * Advances virtual time by [delayTime].
     */
    fun advanceTimeBy(delayTime: Duration)

    /**
     * Collects [flow] into [values] until the end of component test.
     */
    fun <T> collectFlow(flow: Flow<T>, values: MutableList<T>): Job

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
