package ru.mobileup.template

import android.app.Application
import ru.mobileup.template.core.configuration.BuildType
import ru.mobileup.template.core.configuration.Configuration
import ru.mobileup.template.core.configuration.Platform
import ru.mobileup.template.shared.SharedApp
import ru.mobileup.template.shared.SharedAppProvider
import ru.mobileup.template.shared.launchAndroidDebugTools

class App : Application(), SharedAppProvider {

    override lateinit var sharedApp: SharedApp
        private set

    override fun onCreate() {
        super.onCreate()
        sharedApp = SharedApp(getConfiguration())
        sharedApp.launchAndroidDebugTools()
    }

    @Suppress("SENSELESS_COMPARISON")
    private fun getConfiguration() = Configuration(
        platform = Platform(
            application = this,
            debugTools = AndroidDebugToolsImpl(applicationContext)
        ),
        buildType = if (BuildConfig.BUILD_TYPE == "debug") BuildType.Debug else BuildType.Release,
        backendUrl = BuildConfig.BACKEND_URL
    )
}
