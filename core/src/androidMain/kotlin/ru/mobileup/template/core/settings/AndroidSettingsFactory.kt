@file:Suppress("DEPRECATION")

package ru.mobileup.template.core.settings

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.CoroutineDispatcher

internal class AndroidSettingsFactory(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher,
) : SettingsFactory {

    private val settingsCache = mutableMapOf<String, Settings>()
    private val encryptedSettingsCache = mutableMapOf<String, Settings>()

    override fun getSettings(
        name: String,
    ): Settings = synchronized(settingsCache) {
        settingsCache.getOrPut(name) {
            AndroidSettings(
                sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE),
                dispatcher = dispatcher
            )
        }
    }

    override fun getEncryptedSettings(
        name: String,
    ): Settings = synchronized(encryptedSettingsCache) {
        encryptedSettingsCache.getOrPut(name) {
            AndroidSettings(
                sharedPreferences = EncryptedSharedPreferences(
                    context = context,
                    fileName = name,
                    masterKey = MasterKey(context = context),
                ),
                dispatcher = dispatcher
            )
        }
    }
}
