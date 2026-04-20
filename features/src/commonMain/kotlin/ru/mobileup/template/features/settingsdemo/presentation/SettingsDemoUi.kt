package ru.mobileup.template.features.settingsdemo.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.theme.custom.CustomTheme
import ru.mobileup.template.core.utils.LocalBackAction
import ru.mobileup.template.core.utils.withImePadding
import ru.mobileup.template.core.widget.button.AppButton
import ru.mobileup.template.core.widget.button.ButtonType
import ru.mobileup.template.core.widget.checkbox.AppCheckbox
import ru.mobileup.template.core.widget.text_field.AppTextField
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.settings_demo_boolean_label
import ru.mobileup.template.features.generated.resources.settings_demo_clear_storage
import ru.mobileup.template.features.generated.resources.settings_demo_description
import ru.mobileup.template.features.generated.resources.settings_demo_encrypted_description
import ru.mobileup.template.features.generated.resources.settings_demo_encrypted_title
import ru.mobileup.template.features.generated.resources.settings_demo_float_label
import ru.mobileup.template.features.generated.resources.settings_demo_int_label
import ru.mobileup.template.features.generated.resources.settings_demo_load_all
import ru.mobileup.template.features.generated.resources.settings_demo_long_label
import ru.mobileup.template.features.generated.resources.settings_demo_regular_description
import ru.mobileup.template.features.generated.resources.settings_demo_regular_title
import ru.mobileup.template.features.generated.resources.settings_demo_remove_key
import ru.mobileup.template.features.generated.resources.settings_demo_save_all
import ru.mobileup.template.features.generated.resources.settings_demo_string_label
import ru.mobileup.template.features.generated.resources.settings_demo_title

@Composable
fun SettingsDemoUi(
    component: SettingsDemoComponent,
    modifier: Modifier = Modifier,
) {
    val uiState by component.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CustomTheme.colors.background.screen,
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp),
                ) {
                    IconButton(onClick = LocalBackAction.current) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 56.dp),
                        text = stringResource(Res.string.settings_demo_title),
                        style = CustomTheme.typography.title.regular,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(48.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding.withImePadding())
                .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.settings_demo_description),
                style = CustomTheme.typography.body.regular
            )
            SettingsStorageSection(
                title = stringResource(Res.string.settings_demo_regular_title),
                description = stringResource(Res.string.settings_demo_regular_description),
                state = uiState.regularSection,
                storageType = SettingsStorageType.Regular,
                component = component
            )
            SettingsStorageSection(
                title = stringResource(Res.string.settings_demo_encrypted_title),
                description = stringResource(Res.string.settings_demo_encrypted_description),
                state = uiState.encryptedSection,
                storageType = SettingsStorageType.Encrypted,
                component = component
            )
        }
    }
}

@Composable
private fun SettingsStorageSection(
    title: String,
    description: String,
    state: SettingsSectionUiState,
    storageType: SettingsStorageType,
    component: SettingsDemoComponent,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = CustomTheme.typography.title.regular
            )
            Text(
                text = description,
                style = CustomTheme.typography.body.regular
            )
            SettingsTextField(
                label = stringResource(Res.string.settings_demo_string_label),
                value = state.stringValue,
                onValueChange = { component.onStringChanged(storageType, it) },
                onRemoveClick = { component.onRemoveClick(storageType, SettingsField.String) }
            )
            SettingsTextField(
                label = stringResource(Res.string.settings_demo_long_label),
                value = state.longValue,
                errorText = state.longError,
                keyboardType = KeyboardType.Number,
                onValueChange = { component.onLongChanged(storageType, it) },
                onRemoveClick = { component.onRemoveClick(storageType, SettingsField.Long) }
            )
            BooleanField(
                value = state.booleanValue,
                onValueChange = { component.onBooleanChanged(storageType, it) },
                onRemoveClick = { component.onRemoveClick(storageType, SettingsField.Boolean) }
            )
            SettingsTextField(
                label = stringResource(Res.string.settings_demo_int_label),
                value = state.intValue,
                errorText = state.intError,
                keyboardType = KeyboardType.Number,
                onValueChange = { component.onIntChanged(storageType, it) },
                onRemoveClick = { component.onRemoveClick(storageType, SettingsField.Int) }
            )
            SettingsTextField(
                label = stringResource(Res.string.settings_demo_float_label),
                value = state.floatValue,
                errorText = state.floatError,
                keyboardType = KeyboardType.Decimal,
                onValueChange = { component.onFloatChanged(storageType, it) },
                onRemoveClick = { component.onRemoveClick(storageType, SettingsField.Float) }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppButton(
                    text = stringResource(Res.string.settings_demo_save_all),
                    buttonType = ButtonType.Primary,
                    onClick = { component.onSaveClick(storageType) },
                    modifier = Modifier.weight(1f)
                )
                AppButton(
                    text = stringResource(Res.string.settings_demo_load_all),
                    buttonType = ButtonType.Secondary,
                    onClick = { component.onLoadClick(storageType) },
                    modifier = Modifier.weight(1f)
                )
            }
            AppButton(
                text = stringResource(Res.string.settings_demo_clear_storage),
                buttonType = ButtonType.Secondary,
                onClick = { component.onClearClick(storageType) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SettingsTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppTextField(
            text = value,
            onTextChange = onValueChange,
            label = label,
            errorText = errorText,
            isError = errorText != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth()
        )
        AppButton(
            text = stringResource(Res.string.settings_demo_remove_key),
            buttonType = ButtonType.Secondary,
            onClick = onRemoveClick
        )
    }
}

@Composable
private fun BooleanField(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppCheckbox(
                isChecked = value,
                onCheckedChange = onValueChange
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.settings_demo_boolean_label),
                style = CustomTheme.typography.body.regular
            )
        }
        AppButton(
            text = stringResource(Res.string.settings_demo_remove_key),
            buttonType = ButtonType.Secondary,
            onClick = onRemoveClick
        )
    }
}

@Preview
@Composable
private fun SettingsDemoUiPreview() {
    AppTheme {
        SettingsDemoUi(FakeSettingsDemoComponent())
    }
}
