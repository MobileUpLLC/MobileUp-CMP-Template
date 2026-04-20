package ru.mobileup.template.features.settingsdemo.presentation

import com.arkivanov.essenty.backhandler.BackDispatcher
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsDemoComponent : SettingsDemoComponent {

    override val uiState = MutableStateFlow(
        SettingsDemoUiState(
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
    )

    override fun onStringChanged(storageType: SettingsStorageType, value: String) = Unit

    override fun onLongChanged(storageType: SettingsStorageType, value: String) = Unit

    override fun onBooleanChanged(storageType: SettingsStorageType, value: Boolean) = Unit

    override fun onIntChanged(storageType: SettingsStorageType, value: String) = Unit

    override fun onFloatChanged(storageType: SettingsStorageType, value: String) = Unit

    override fun onSaveClick(storageType: SettingsStorageType) = Unit

    override fun onLoadClick(storageType: SettingsStorageType) = Unit

    override fun onRemoveClick(storageType: SettingsStorageType, field: SettingsField) = Unit

    override fun onClearClick(storageType: SettingsStorageType) = Unit
}
