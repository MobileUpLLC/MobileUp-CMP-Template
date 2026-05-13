package ru.mobileup.template.core

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import me.aartikov.replica.client.ReplicaClient
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.mobileup.template.core.configuration.BuildType
import ru.mobileup.template.core.configuration.Configuration
import ru.mobileup.template.core.error_handling.ErrorHandler
import ru.mobileup.template.core.message.data.MessageService
import ru.mobileup.template.core.message.data.MessageServiceImpl
import ru.mobileup.template.core.message.presentation.MessageComponent
import ru.mobileup.template.core.message.presentation.RealMessageComponent
import ru.mobileup.template.core.network.BackendUrl
import ru.mobileup.template.core.network.NetworkApiFactory

fun coreModule(configuration: Configuration) = module {
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate) }
    single { ReplicaClient(getOrNull()) }
    single<MessageService> { MessageServiceImpl() }
    single {
        ErrorHandler(
            messageService = get(),
            showDebugInfo = configuration.buildType == BuildType.Debug
        )
    }
    single {
        NetworkApiFactory(
            backendUrl = BackendUrl.getMainUrl(configuration.backend),
            httpClientEngine = get(),
            ktorLogger = getOrNull()
        )
    }
}

expect fun platformCoreModule(configuration: Configuration): Module

fun ComponentFactory.createMessageComponent(
    componentContext: ComponentContext
): MessageComponent {
    return RealMessageComponent(componentContext, get())
}
