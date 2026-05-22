package ru.mobileup.template.core.settings

interface SettingsFactory {

    fun getSettings(name: String): Settings

    fun getEncryptedSettings(name: String): Settings
}
