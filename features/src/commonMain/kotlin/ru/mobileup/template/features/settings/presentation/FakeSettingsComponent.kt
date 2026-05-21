package ru.mobileup.template.features.settings.presentation

import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsComponent : SettingsComponent {

    override val uiState = MutableStateFlow(SettingsUiState.FAKE)

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
