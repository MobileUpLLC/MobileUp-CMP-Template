package ru.mobileup.template.core_testing

import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestDispatcher
import me.aartikov.replica.client.ReplicaClient
import me.aartikov.replica.network.NetworkConnectivityProvider
import org.koin.core.Koin
import org.koin.dsl.module
import ru.mobileup.template.core.error_handling.ErrorHandler
import ru.mobileup.template.core.message.data.MessageService
import ru.mobileup.template.core.network.BackendUrl
import ru.mobileup.template.core.network.NetworkApiFactory
import ru.mobileup.template.core.permissions.PermissionService
import ru.mobileup.template.core_testing.message.data.TestMessageService
import ru.mobileup.template.core_testing.network.MockServer
import ru.mobileup.template.core_testing.network.TestNetworkConnectivityProvider
import ru.mobileup.template.core_testing.network.createMockHttpEngine
import ru.mobileup.template.core_testing.permissions.TestPermissionService

fun coreTestModule(testDispatcher: TestDispatcher) = module {
    single<TestDispatcher> { testDispatcher }

    single { ErrorHandler(get(), showDebugInfo = false) }
    single<MessageService> { TestMessageService() }
    single<PermissionService> { TestPermissionService() }

    single<HttpClientEngine> {
        createMockHttpEngine(get(), testDispatcher)
    }
    single { MockServer() }
    single {
        NetworkApiFactory(
            backendUrl = BackendUrl("https://test/"),
            httpClientEngine = get()
        )
    }

    single<NetworkConnectivityProvider> { TestNetworkConnectivityProvider() }
    single {
        ReplicaClient(
            networkConnectivityProvider = get(),
            coroutineScope = CoroutineScope(testDispatcher),
        )
    }
}

val Koin.testMessageService: TestMessageService
    get() = get<MessageService>() as TestMessageService
