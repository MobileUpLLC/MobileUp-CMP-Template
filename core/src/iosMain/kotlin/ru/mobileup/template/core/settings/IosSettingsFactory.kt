package ru.mobileup.template.core.settings

import kotlinx.coroutines.CoroutineDispatcher
import platform.Foundation.NSUserDefaults

class IosSettingsFactory(
    private val dispatcher: CoroutineDispatcher
) : SettingsFactory {

    override fun createSettings(name: String): Settings = IosUserDefaultsSettings(
        userDefaults = NSUserDefaults(name),
        dispatcher = dispatcher
    )

    override fun createEncryptedSettings(name: String): Settings =
        IosKeychainSettings(name, dispatcher)
}
