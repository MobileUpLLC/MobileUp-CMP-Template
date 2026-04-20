package ru.mobileup.template.features.settingsdemo.presentation

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mobileup.template.core.message.data.MessageService
import ru.mobileup.template.core.message.domain.Message
import ru.mobileup.template.core.settings.Settings
import ru.mobileup.template.core.settings.SettingsFactory
import ru.mobileup.template.core.utils.componentScope
import ru.mobileup.template.core.utils.desc

class RealSettingsDemoComponent(
    componentContext: ComponentContext,
    settingsFactory: SettingsFactory,
    private val messageService: MessageService,
) : ComponentContext by componentContext, SettingsDemoComponent {

    private val regularSettings = settingsFactory.createSettings("settings_demo_regular")
    private val encryptedSettings = settingsFactory.createEncryptedSettings("settings_demo_encrypted")

    override val uiState = MutableStateFlow(SettingsDemoUiState())

    override fun onStringChanged(storageType: SettingsStorageType, value: String) {
        updateSection(storageType) { it.copy(stringValue = value) }
    }

    override fun onLongChanged(storageType: SettingsStorageType, value: String) {
        updateSection(storageType) { it.copy(longValue = value, longError = null) }
    }

    override fun onBooleanChanged(storageType: SettingsStorageType, value: Boolean) {
        updateSection(storageType) { it.copy(booleanValue = value) }
    }

    override fun onIntChanged(storageType: SettingsStorageType, value: String) {
        updateSection(storageType) { it.copy(intValue = value, intError = null) }
    }

    override fun onFloatChanged(storageType: SettingsStorageType, value: String) {
        updateSection(storageType) { it.copy(floatValue = value, floatError = null) }
    }

    override fun onSaveClick(storageType: SettingsStorageType) {
        val section = uiState.value.section(storageType)
        val validatedSection = validateSection(storageType, section) ?: return

        launchStorageAction(storageType) { settings ->
            settings.putString(SettingsField.String.key, section.stringValue)
            settings.putLong(SettingsField.Long.key, validatedSection.longValue)
            settings.putBoolean(SettingsField.Boolean.key, section.booleanValue)
            settings.putInt(SettingsField.Int.key, validatedSection.intValue)
            settings.putFloat(SettingsField.Float.key, validatedSection.floatValue)

            updateSection(storageType) {
                it.copy(
                    longError = null,
                    intError = null,
                    floatError = null
                )
            }

            showMessage("${storageType.label} settings saved")
        }
    }

    override fun onLoadClick(storageType: SettingsStorageType) {
        launchStorageAction(storageType) { settings ->
            val stringValue = settings.getString(SettingsField.String.key).orEmpty()
            val longValue = settings.getLong(SettingsField.Long.key)?.toString().orEmpty()
            val booleanValue = settings.getBoolean(SettingsField.Boolean.key) ?: false
            val intValue = settings.getInt(SettingsField.Int.key)?.toString().orEmpty()
            val floatValue = settings.getFloat(SettingsField.Float.key)?.toString().orEmpty()

            updateSection(storageType) {
                it.copy(
                    stringValue = stringValue,
                    longValue = longValue,
                    booleanValue = booleanValue,
                    intValue = intValue,
                    floatValue = floatValue,
                    longError = null,
                    intError = null,
                    floatError = null
                )
            }

            showMessage("${storageType.label} settings loaded")
        }
    }

    override fun onRemoveClick(storageType: SettingsStorageType, field: SettingsField) {
        launchStorageAction(storageType) { settings ->
            settings.remove(field.key)
            updateSection(storageType) { it.removeField(field) }
            showMessage("${storageType.label} settings key '${field.key}' removed")
        }
    }

    override fun onClearClick(storageType: SettingsStorageType) {
        launchStorageAction(storageType) { settings ->
            settings.clear()
            updateSection(storageType) { SettingsSectionUiState() }
            showMessage("${storageType.label} settings cleared")
        }
    }

    private fun validateSection(
        storageType: SettingsStorageType,
        section: SettingsSectionUiState,
    ): ValidatedSection? {
        val errors = ValidationErrors(
            longError = validateNumber(section.longValue, "Long"),
            intError = validateNumber(section.intValue, "Int"),
            floatError = validateNumber(section.floatValue, "Float")
        )

        updateSection(storageType) {
            it.copy(
                longError = errors.longError,
                intError = errors.intError,
                floatError = errors.floatError
            )
        }

        if (errors.hasErrors) {
            showMessage("Fix invalid values before saving")
            return null
        }

        return ValidatedSection(
            longValue = requireNotNull(section.longValue.toLongOrNull()),
            intValue = requireNotNull(section.intValue.toIntOrNull()),
            floatValue = requireNotNull(section.floatValue.toFloatOrNull())
        )
    }

    private fun validateNumber(value: String, typeName: String): String? {
        if (value.isBlank()) return "Required"

        return when (typeName) {
            "Long" -> if (value.toLongOrNull() == null) "Invalid Long" else null
            "Int" -> if (value.toIntOrNull() == null) "Invalid Int" else null
            "Float" -> if (value.toFloatOrNull() == null) "Invalid Float" else null
            else -> null
        }
    }

    private fun updateSection(
        storageType: SettingsStorageType,
        transform: (SettingsSectionUiState) -> SettingsSectionUiState,
    ) {
        uiState.update { state ->
            when (storageType) {
                SettingsStorageType.Regular -> state.copy(
                    regularSection = transform(state.regularSection)
                )

                SettingsStorageType.Encrypted -> state.copy(
                    encryptedSection = transform(state.encryptedSection)
                )
            }
        }
    }

    private fun launchStorageAction(
        storageType: SettingsStorageType,
        action: suspend (Settings) -> Unit,
    ) {
        componentScope.launch {
            runCatching {
                action(storage(storageType))
            }.onFailure { throwable ->
                showMessage(
                    "${storageType.label} settings error: ${throwable.message ?: "Unknown error"}"
                )
            }
        }
    }

    private fun storage(storageType: SettingsStorageType): Settings = when (storageType) {
        SettingsStorageType.Regular -> regularSettings
        SettingsStorageType.Encrypted -> encryptedSettings
    }

    private fun showMessage(text: String) {
        messageService.showMessage(Message(text = text.desc()))
    }

    private val SettingsStorageType.label: String
        get() = when (this) {
            SettingsStorageType.Regular -> "Regular"
            SettingsStorageType.Encrypted -> "Encrypted"
        }

    private fun SettingsDemoUiState.section(storageType: SettingsStorageType): SettingsSectionUiState =
        when (storageType) {
            SettingsStorageType.Regular -> regularSection
            SettingsStorageType.Encrypted -> encryptedSection
        }

    private fun SettingsSectionUiState.removeField(field: SettingsField): SettingsSectionUiState =
        when (field) {
            SettingsField.String -> copy(stringValue = "")
            SettingsField.Long -> copy(longValue = "", longError = null)
            SettingsField.Boolean -> copy(booleanValue = false)
            SettingsField.Int -> copy(intValue = "", intError = null)
            SettingsField.Float -> copy(floatValue = "", floatError = null)
        }

    private data class ValidatedSection(
        val longValue: Long,
        val intValue: Int,
        val floatValue: Float,
    )

    private data class ValidationErrors(
        val longError: String?,
        val intError: String?,
        val floatError: String?,
    ) {
        val hasErrors: Boolean = longError != null || intError != null || floatError != null
    }
}
