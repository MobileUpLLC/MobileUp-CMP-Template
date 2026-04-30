package ru.mobileup.template.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module
import ru.mobileup.template.core.configuration.BuildType
import ru.mobileup.template.core.configuration.Configuration
import ru.mobileup.template.core.external_app.ExternalAppService
import ru.mobileup.template.core.external_app.IosExternalAppService
import ru.mobileup.template.core.network.createKtorLogger
import ru.mobileup.template.core.network.createOkHttpEngine
import ru.mobileup.template.core.permissions.IosPermissionService
import ru.mobileup.template.core.permissions.PermissionService
import ru.mobileup.template.core.settings.IosSettingsFactory
import ru.mobileup.template.core.settings.SettingsFactory

actual fun platformCoreModule(configuration: Configuration) = module {
    single { createOkHttpEngine() }
    if (configuration.buildType == BuildType.Debug) {
        single { createKtorLogger() }
    }
    single<SettingsFactory> { IosSettingsFactory(Dispatchers.IO) }
    single<PermissionService> { IosPermissionService() }
    single<ExternalAppService> { IosExternalAppService() }
}
