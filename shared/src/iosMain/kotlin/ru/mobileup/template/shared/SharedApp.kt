package ru.mobileup.template.shared

import androidx.compose.runtime.CompositionLocalProvider
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.ApplicationLifecycle
import ru.mobileup.template.core.configuration.LocalPlatform
import ru.mobileup.template.core.configuration.LocalPlatformType
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.utils.LocalBackAction
import ru.mobileup.template.core.utils.RootViewController
import ru.mobileup.template.features.root.presentation.RootUi

fun SharedApp.createRootViewController(): RootViewController {
    val backDispatcher = BackDispatcher()
    val componentContext = DefaultComponentContext(
        lifecycle = ApplicationLifecycle(),
        stateKeeper = null,
        instanceKeeper = null,
        backHandler = backDispatcher
    )
    val rootComponent = createRootComponent(componentContext)

    return RootViewController(backDispatcher) {
        CompositionLocalProvider(
            LocalPlatformType provides platform.type,
            LocalPlatform provides platform,
            LocalBackAction provides backDispatcher::back
        ) {
            AppTheme {
                RootUi(rootComponent)
            }
        }
    }
}
