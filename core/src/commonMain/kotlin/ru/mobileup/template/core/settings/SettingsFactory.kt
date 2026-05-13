package ru.mobileup.template.core.settings

interface SettingsFactory {

    fun createSettings(name: String): Settings

    fun createEncryptedSettings(name: String): Settings
}
