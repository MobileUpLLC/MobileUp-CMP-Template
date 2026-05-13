package ru.mobileup.template.core.settings

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import platform.Foundation.NSUserDefaults

internal class IosUserDefaultsSettings(
    private val userDefaults: NSUserDefaults,
    private val dispatcher: CoroutineDispatcher,
) : Settings {

    private suspend fun <T> getValue(
        key: String,
        getter: NSUserDefaults.(String) -> T,
    ): T? = withContext(dispatcher) {
        if (userDefaults.objectForKey(key) != null) {
            userDefaults.getter(key)
        } else {
            null
        }
    }

    override suspend fun getString(key: String): String? =
        getValue(key, NSUserDefaults::stringForKey)

    override suspend fun getLong(key: String): Long? =
        getValue(key) { integerForKey(it) }

    override suspend fun getBoolean(key: String): Boolean? =
        getValue(key, NSUserDefaults::boolForKey)

    override suspend fun getInt(key: String): Int? =
        getValue(key) { integerForKey(it).toInt() }

    override suspend fun getFloat(key: String): Float? =
        getValue(key, NSUserDefaults::floatForKey)

    override suspend fun getDouble(key: String): Double? =
        getValue(key, NSUserDefaults::doubleForKey)

    override suspend fun putString(key: String, value: String) = withContext(dispatcher) {
        userDefaults.setObject(value, key)
    }

    override suspend fun putLong(key: String, value: Long) = withContext(dispatcher) {
        userDefaults.setInteger(value, key)
    }

    override suspend fun putBoolean(key: String, value: Boolean) = withContext(dispatcher) {
        userDefaults.setBool(value, key)
    }

    override suspend fun putInt(key: String, value: Int) = withContext(dispatcher) {
        userDefaults.setInteger(value.toLong(), key)
    }

    override suspend fun putFloat(key: String, value: Float) = withContext(dispatcher) {
        userDefaults.setFloat(value, key)
    }

    override suspend fun putDouble(key: String, value: Double) = withContext(dispatcher) {
        userDefaults.setDouble(value, key)
    }

    override suspend fun remove(key: String) = withContext(dispatcher) {
        userDefaults.removeObjectForKey(key)
    }

    override suspend fun clear() = withContext(dispatcher) {
        userDefaults.dictionaryRepresentation().keys.forEach { key ->
            userDefaults.removeObjectForKey(key as String)
        }
    }
}
