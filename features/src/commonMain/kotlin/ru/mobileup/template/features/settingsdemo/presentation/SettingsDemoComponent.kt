package ru.mobileup.template.features.settingsdemo.presentation

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.template.core.utils.PredictiveBackComponent

interface SettingsDemoComponent {

    val uiState: StateFlow<SettingsDemoUiState>

    fun onStringChanged(storageType: SettingsStorageType, value: String)

    fun onLongChanged(storageType: SettingsStorageType, value: String)

    fun onBooleanChanged(storageType: SettingsStorageType, value: Boolean)

    fun onIntChanged(storageType: SettingsStorageType, value: String)

    fun onFloatChanged(storageType: SettingsStorageType, value: String)

    fun onSaveClick(storageType: SettingsStorageType)

    fun onLoadClick(storageType: SettingsStorageType)

    fun onRemoveClick(storageType: SettingsStorageType, field: SettingsField)

    fun onClearClick(storageType: SettingsStorageType)
}

enum class SettingsStorageType {
    Regular,
    Encrypted
}

enum class SettingsField(val key: String) {
    String("string"),
    Long("long"),
    Boolean("boolean"),
    Int("int"),
    Float("float")
}

data class SettingsDemoUiState(
    val regularSection: SettingsSectionUiState = SettingsSectionUiState(),
    val encryptedSection: SettingsSectionUiState = SettingsSectionUiState(),
)

data class SettingsSectionUiState(
    val stringValue: String = "",
    val longValue: String = "",
    val booleanValue: Boolean = false,
    val intValue: String = "",
    val floatValue: String = "",
    val longError: String? = null,
    val intError: String? = null,
    val floatError: String? = null,
)
