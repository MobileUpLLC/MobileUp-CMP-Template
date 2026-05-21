package ru.mobileup.template.features.settings.presentation

import kotlinx.coroutines.flow.StateFlow

interface SettingsComponent {

    val uiState: StateFlow<SettingsUiState>

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

data class SettingsUiState(
    val regularSection: SettingsSectionUiState = SettingsSectionUiState(),
    val encryptedSection: SettingsSectionUiState = SettingsSectionUiState(),
) {

    companion object {
        val FAKE = SettingsUiState(
            regularSection = SettingsSectionUiState(
                stringValue = "Ash",
                longValue = "25",
                booleanValue = true,
                intValue = "7",
                floatValue = "12.5"
            ),
            encryptedSection = SettingsSectionUiState(
                stringValue = "Pikachu",
                longValue = "999",
                booleanValue = false,
                intValue = "42",
                floatValue = "3.14"
            )
        )
    }
}

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
