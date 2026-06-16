package ru.mobileup.template.shared

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.arkivanov.decompose.retainedComponent
import com.arkivanov.essenty.lifecycle.asEssentyLifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import me.aartikov.replica.client.ReplicaClient
import ru.mobileup.template.core.activity.ActivityProvider
import ru.mobileup.template.core.configuration.LocalPlatformType
import ru.mobileup.template.core.debug_tools.AndroidDebugTools
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.utils.AndroidSystemBarIconsColorHandler
import ru.mobileup.template.core.utils.LocalBackAction
import ru.mobileup.template.core.utils.LocalSystemBarIconsColorHandler
import ru.mobileup.template.features.root.presentation.RootUi

val Application.sharedApp get() = (this as SharedAppProvider).sharedApp

fun SharedApp.launchInActivity(activity: ComponentActivity) {
    val activityProvider = get<ActivityProvider>()
    activityProvider.attachActivity(activity)
    activity.lifecycle.asEssentyLifecycle().doOnDestroy {
        activityProvider.detachActivity()
    }

    val rootComponent = activity.retainedComponent { componentContext ->
        createRootComponent(componentContext)
    }

    val systemBarIconsColorHandler = AndroidSystemBarIconsColorHandler(activity.window)
    val backAction = activity.onBackPressedDispatcher::onBackPressed

    activity.setContent {
        CompositionLocalProvider(
            LocalPlatformType provides platform.type,
            LocalSystemBarIconsColorHandler provides systemBarIconsColorHandler,
            LocalBackAction provides backAction
        ) {
            AppTheme {
                RootUi(rootComponent)
            }
        }
    }
}

fun SharedApp.launchAndroidDebugTools() {
    val replicaClient = get<ReplicaClient>()
    val androidDebugTools = get<AndroidDebugTools>()
    androidDebugTools.launch(replicaClient)
}
