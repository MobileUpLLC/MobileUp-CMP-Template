package ru.mobileup.template.core_testing.test_services

import ru.mobileup.template.core.settings.Settings

/**
 * In-memory test implementation of [Settings].
 */
class TestSettings : Settings {

    private val values = mutableMapOf<String, Any>()

    override suspend fun getString(key: String): String? =
        values[key] as? String

    override suspend fun getLong(key: String): Long? =
        values[key] as? Long

    override suspend fun getBoolean(key: String): Boolean? =
        values[key] as? Boolean

    override suspend fun getInt(key: String): Int? =
        values[key] as? Int

    override suspend fun getFloat(key: String): Float? =
        values[key] as? Float

    override suspend fun getDouble(key: String): Double? =
        values[key] as? Double

    override suspend fun putString(key: String, value: String) {
        values[key] = value
    }

    override suspend fun putLong(key: String, value: Long) {
        values[key] = value
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        values[key] = value
    }

    override suspend fun putInt(key: String, value: Int) {
        values[key] = value
    }

    override suspend fun putFloat(key: String, value: Float) {
        values[key] = value
    }

    override suspend fun putDouble(key: String, value: Double) {
        values[key] = value
    }

    override suspend fun remove(key: String) {
        values.remove(key)
    }

    override suspend fun clear() {
        values.clear()
    }
}
