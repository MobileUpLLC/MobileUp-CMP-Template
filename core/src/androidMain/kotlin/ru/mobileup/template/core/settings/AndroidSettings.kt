package ru.mobileup.template.core.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class AndroidSettings(
    private val sharedPreferences: SharedPreferences,
    private val dispatcher: CoroutineDispatcher,
) : Settings {

    private suspend fun <T> getValue(
        key: String,
        defaultValue: T,
        getter: SharedPreferences.(String, T) -> T,
    ): T? = withContext(dispatcher) {
        if (sharedPreferences.contains(key)) sharedPreferences.getter(key, defaultValue) else null
    }

    override suspend fun getString(key: String): String? =
        getValue(key, null, SharedPreferences::getString)

    override suspend fun getLong(key: String): Long? =
        getValue(key, 0L, SharedPreferences::getLong)

    override suspend fun getBoolean(key: String): Boolean? =
        getValue(key, false, SharedPreferences::getBoolean)

    override suspend fun getInt(key: String): Int? =
        getValue(key, 0, SharedPreferences::getInt)

    override suspend fun getFloat(key: String): Float? =
        getValue(key, 0f, SharedPreferences::getFloat)

    override suspend fun getDouble(key: String): Double? =
        getLong(key)?.let { Double.fromBits(it) }

    private suspend fun <T> putValue(
        key: String,
        value: T,
        editor: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor,
    ) = withContext(dispatcher) {
        sharedPreferences.edit { editor(key, value) }
    }

    override suspend fun putString(key: String, value: String) =
        putValue(key, value, SharedPreferences.Editor::putString)

    override suspend fun putLong(key: String, value: Long) =
        putValue(key, value, SharedPreferences.Editor::putLong)

    override suspend fun putBoolean(key: String, value: Boolean) =
        putValue(key, value, SharedPreferences.Editor::putBoolean)

    override suspend fun putInt(key: String, value: Int) =
        putValue(key, value, SharedPreferences.Editor::putInt)

    override suspend fun putFloat(key: String, value: Float) =
        putValue(key, value, SharedPreferences.Editor::putFloat)

    override suspend fun putDouble(key: String, value: Double) =
        putLong(key, value.toBits())

    override suspend fun remove(key: String) = withContext(dispatcher) {
        sharedPreferences.edit { remove(key) }
    }

    override suspend fun clear() = withContext(dispatcher) {
        sharedPreferences.edit { clear() }
    }
}
