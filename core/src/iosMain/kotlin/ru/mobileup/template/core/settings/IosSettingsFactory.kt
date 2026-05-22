package ru.mobileup.template.core.settings

import kotlinx.coroutines.CoroutineDispatcher
import platform.Foundation.NSLock
import platform.Foundation.NSUserDefaults

class IosSettingsFactory(
    private val dispatcher: CoroutineDispatcher
) : SettingsFactory {

    private val cacheLock = NSLock()
    private val settingsCache = mutableMapOf<String, Settings>()
    private val encryptedSettingsCache = mutableMapOf<String, Settings>()

    override fun getSettings(name: String): Settings = locked {
        settingsCache.getOrPut(name) {
            IosUserDefaultsSettings(
                userDefaults = NSUserDefaults(name),
                dispatcher = dispatcher
            )
        }
    }

    override fun getEncryptedSettings(name: String): Settings = locked {
        encryptedSettingsCache.getOrPut(name) {
            IosKeychainSettings(name, dispatcher)
        }
    }

    private fun <T> locked(action: () -> T): T {
        cacheLock.lock()
        return try {
            action()
        } finally {
            cacheLock.unlock()
        }
    }
}
