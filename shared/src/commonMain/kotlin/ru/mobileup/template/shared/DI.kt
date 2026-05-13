package ru.mobileup.template.shared

import org.koin.core.Koin
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.core.configuration.Configuration
import ru.mobileup.template.core.coreModule
import ru.mobileup.template.core.platformCoreModule
import ru.mobileup.template.features.featureModules

internal fun createKoin(configuration: Configuration): Koin {
    return Koin().apply {
        loadModules(
            listOf(
                coreModule(configuration),
                platformCoreModule(configuration),
            ) + featureModules
        )
        declare(ComponentFactory(this))
        createEagerInstances()
    }
}
