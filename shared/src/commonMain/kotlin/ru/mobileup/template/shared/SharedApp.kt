package ru.mobileup.template.shared

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.arkivanov.decompose.ComponentContext
import org.koin.core.Koin
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.core.configuration.BuildType
import ru.mobileup.template.core.configuration.Configuration
import ru.mobileup.template.core.configuration.PlatformType
import ru.mobileup.template.features.root.createRootComponent
import ru.mobileup.template.features.root.presentation.RootComponent

class SharedApp(configuration: Configuration) {

    internal val platformType: PlatformType = configuration.platform.type

    private val koin: Koin
    private val componentFactory: ComponentFactory

    init {
        if (configuration.buildType == BuildType.Release) {
            Logger.setMinSeverity(Severity.Assert)
        }
        koin = createKoin(configuration)
        componentFactory = ComponentFactory(koin)
    }

    fun createRootComponent(componentContext: ComponentContext): RootComponent {
        return componentFactory.createRootComponent(componentContext)
    }

    internal inline fun <reified T : Any> get(): T = koin.get<T>()
}

interface SharedAppProvider {
    val sharedApp: SharedApp
}
