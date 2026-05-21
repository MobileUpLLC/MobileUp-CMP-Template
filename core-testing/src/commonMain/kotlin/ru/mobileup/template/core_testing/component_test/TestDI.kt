package ru.mobileup.template.core_testing.component_test

import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import me.aartikov.replica.client.ReplicaClient
import me.aartikov.replica.network.NetworkConnectivityProvider
import org.koin.core.Koin
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.mobileup.template.core.error_handling.ErrorHandler
import ru.mobileup.template.core.external_app.ExternalAppService
import ru.mobileup.template.core.message.data.MessageService
import ru.mobileup.template.core.network.NetworkApiFactory
import ru.mobileup.template.core.permissions.PermissionService
import ru.mobileup.template.core_testing.network.MockServer
import ru.mobileup.template.core_testing.network.TestNetworkConnectivityProvider
import ru.mobileup.template.core_testing.network.createMockHttpEngine
import ru.mobileup.template.core_testing.test_services.TestExternalAppService
import ru.mobileup.template.core_testing.test_services.TestMessageService
import ru.mobileup.template.core_testing.test_services.TestPermissionService
import ru.mobileup.template.core_testing.network.TestReplicaTimeProvider

internal fun createKoin(
    testScheduler: TestCoroutineScheduler,
    testDispatcher: TestDispatcher,
    replicaBehaviourDispatcher: TestDispatcher,
    featureModules: List<Module>
): Koin {
    return Koin().apply {
        loadModules(coreTestModule(testScheduler, testDispatcher, replicaBehaviourDispatcher) + featureModules)
        createEagerInstances()
    }
}

private fun coreTestModule(
    testScheduler: TestCoroutineScheduler,
    testDispatcher: TestDispatcher,
    replicaBehaviourDispatcher: TestDispatcher
) = module {
    single { ErrorHandler(get(), showDebugInfo = false) }
    single<MessageService> { TestMessageService() }
    single<PermissionService> { TestPermissionService() }
    single<ExternalAppService> { TestExternalAppService() }

    single<HttpClientEngine> {
        createMockHttpEngine(get(), testDispatcher)
    }
    single { MockServer() }
    single {
        NetworkApiFactory(
            backendUrl = "https://test/",
            httpClientEngine = get()
        )
    }

    single<NetworkConnectivityProvider> { TestNetworkConnectivityProvider() }
    single {
        ReplicaClient(
            networkConnectivityProvider = get(),
            timeProvider = TestReplicaTimeProvider(testScheduler),
            mainDispatcher = testDispatcher,
            behaviourDispatcher = replicaBehaviourDispatcher
        )
    }
}
