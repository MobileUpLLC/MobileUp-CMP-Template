package ru.mobileup.template.core.settings

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class IosKeychainSettings(
    name: String,
    private val dispatcher: CoroutineDispatcher
) : Settings {

    val impl = KeychainWrapper(name)

    override suspend fun getString(key: String): String? = withContext(dispatcher) {
        impl.getString(key)
    }

    override suspend fun getLong(key: String): Long? = withContext(dispatcher) {
        impl.getLong(key)
    }

    override suspend fun getBoolean(key: String): Boolean? = withContext(dispatcher) {
        impl.getBoolean(key)
    }

    override suspend fun getInt(key: String): Int? = withContext(dispatcher) {
        impl.getInt(key)
    }

    override suspend fun getFloat(key: String): Float? = withContext(dispatcher) {
        impl.getFloat(key)
    }

    override suspend fun getDouble(key: String): Double? = withContext(dispatcher) {
        impl.getDouble(key)
    }

    override suspend fun putString(key: String, value: String) = withContext(dispatcher) {
        impl.putString(key, value)
    }

    override suspend fun putLong(key: String, value: Long) = withContext(dispatcher) {
        impl.putLong(key, value)
    }

    override suspend fun putBoolean(key: String, value: Boolean) = withContext(dispatcher) {
        impl.putBoolean(key, value)
    }

    override suspend fun putInt(key: String, value: Int) = withContext(dispatcher) {
        impl.putInt(key, value)
    }

    override suspend fun putFloat(key: String, value: Float) = withContext(dispatcher) {
        impl.putFloat(key, value)
    }

    override suspend fun putDouble(key: String, value: Double) = withContext(dispatcher) {
        impl.putDouble(key, value)
    }

    override suspend fun remove(key: String) = withContext(dispatcher) {
        impl.remove(key)
    }

    override suspend fun clear() = withContext(dispatcher) {
        impl.clear()
    }
}
