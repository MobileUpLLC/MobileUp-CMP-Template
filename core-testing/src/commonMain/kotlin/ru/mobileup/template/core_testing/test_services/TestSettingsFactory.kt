package ru.mobileup.template.core_testing.test_services

import ru.mobileup.template.core.settings.Settings
import ru.mobileup.template.core.settings.SettingsFactory

/**
 * In-memory test implementation of [SettingsFactory].
 */
class TestSettingsFactory : SettingsFactory {

    private val settingsCache = mutableMapOf<String, Settings>()
    private val encryptedSettingsCache = mutableMapOf<String, Settings>()

    override fun getSettings(name: String): Settings =
        settingsCache.getOrPut(name) { TestSettings() }

    override fun getEncryptedSettings(name: String): Settings =
        encryptedSettingsCache.getOrPut(name) { TestSettings() }
}
