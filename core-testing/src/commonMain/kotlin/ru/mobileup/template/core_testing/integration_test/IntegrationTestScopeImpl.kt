package ru.mobileup.template.core_testing.integration_test

import com.arkivanov.essenty.lifecycle.Lifecycle
import io.kotest.core.test.TestScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import me.aartikov.replica.network.NetworkConnectivityProvider
import org.koin.core.Koin
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.core.external_app.ExternalAppService
import ru.mobileup.template.core.message.data.MessageService
import ru.mobileup.template.core.permissions.PermissionService
import ru.mobileup.template.core_testing.network.MockServer
import ru.mobileup.template.core_testing.network.TestNetworkConnectivityProvider
import ru.mobileup.template.core_testing.test_services.TestExternalAppService
import ru.mobileup.template.core_testing.test_services.TestMessageService
import ru.mobileup.template.core_testing.test_services.TestPermissionService
import kotlin.time.Duration

/**
 * Default implementation of [IntegrationTestScope].
 *
 * Bridges Kotest test scope, Koin graph, and coroutine test scheduler.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class IntegrationTestScopeImpl(
    koin: Koin,
    private val kotestScope: TestScope,
    private val testScheduler: TestCoroutineScheduler,
    private val replicaBehaviourScheduler: TestCoroutineScheduler,
    private val testDispatcher: TestDispatcher
) : IntegrationTestScope, TestScope by kotestScope {

    override val mockServer: MockServer = koin.get()

    override val messageService: TestMessageService =
        koin.get<MessageService>() as TestMessageService

    override val permissionService: TestPermissionService =
        koin.get<PermissionService>() as TestPermissionService

    override val externalAppService: TestExternalAppService =
        koin.get<ExternalAppService>() as TestExternalAppService

    override val networkConnectivityProvider: TestNetworkConnectivityProvider =
        koin.get<NetworkConnectivityProvider>() as TestNetworkConnectivityProvider

    private val componentFactory: ComponentFactory = koin.get()

    override fun advanceUntilIdle() {
        val startTime = testScheduler.currentTime
        testScheduler.advanceUntilIdle()
        val endTime = testScheduler.currentTime
        replicaBehaviourScheduler.advanceTimeBy(endTime - startTime)
    }

    override fun advanceTimeBy(delayTime: Duration) {
        testScheduler.advanceTimeBy(delayTime)
        replicaBehaviourScheduler.advanceTimeBy(delayTime)
    }

    override fun <T> setupComponent(
        targetState: Lifecycle.State,
        create: ComponentFactory.(TestComponentContext) -> T
    ): T {
        return setupComponentWithContext(targetState, create).first
    }

    override fun <T> setupComponentWithContext(
        targetState: Lifecycle.State,
        create: ComponentFactory.(TestComponentContext) -> T
    ): Pair<T, TestComponentContext> {
        val lifecycle = TestComponentContext(testDispatcher)
        val component = componentFactory.create(lifecycle)
        lifecycle.moveToState(targetState)
        return component to lifecycle
    }
}
