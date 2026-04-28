package ru.mobileup.template.features.pokemons.presentation.dialogs_demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.mobileup.template.core.dialog.BottomSheet
import ru.mobileup.template.core.dialog.Dialog
import ru.mobileup.template.core.dialog.standard.StandardDialog
import ru.mobileup.template.core.message.presentation.noOverlapByMessage
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.theme.custom.CustomTheme
import ru.mobileup.template.core.utils.plus
import ru.mobileup.template.core.widget.toolbar.AppToolbar
import ru.mobileup.template.core.widget.button.AppButton
import ru.mobileup.template.core.widget.button.ButtonType
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_custom_dialog_button
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_dismissible_sheet_button
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_message_sheet_button
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_non_dismissible_sheet_button
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_standard_dialog_button
import ru.mobileup.template.features.generated.resources.pokemons_dialogs_demo_title

@Composable
fun DialogsDemoUi(
    component: DialogsDemoComponent,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppToolbar(
                title = stringResource(Res.string.pokemons_dialogs_demo_title),
                showBackButton = true,
                onBackClick = component::onBackClick
            )
        }
    ) { innerPadding ->
        DialogsDemoContent(
            component = component,
            innerPadding = innerPadding
        )
    }

    StandardDialog(component.standardDialogControl)

    Dialog(component.customDialogControl) { data ->
        CustomDialogContent(
            data = data,
            onCancelClick = component::onCustomDialogCancelClick
        )
    }

    BottomSheet(component.bottomSheetControl) { data ->
        DemoBottomSheetContent(
            data = data,
            onCancelClick = component::onBottomSheetCancelClick,
            onShowMessageClick = component::onShowMessageOverSheetClick
        )
    }
}

@Composable
private fun DialogsDemoContent(
    component: DialogsDemoComponent,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(innerPadding + PaddingValues(16.dp)),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.pokemons_dialogs_demo_standard_dialog_button),
            buttonType = ButtonType.Primary,
            onClick = component::onStandardDialogClick
        )

        AppButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.pokemons_dialogs_demo_custom_dialog_button),
            buttonType = ButtonType.Secondary,
            onClick = component::onCustomDialogClick
        )

        AppButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.pokemons_dialogs_demo_dismissible_sheet_button),
            buttonType = ButtonType.Primary,
            onClick = component::onDismissibleBottomSheetClick
        )

        AppButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.pokemons_dialogs_demo_non_dismissible_sheet_button),
            buttonType = ButtonType.Secondary,
            onClick = component::onNonDismissibleBottomSheetClick
        )

        AppButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.pokemons_dialogs_demo_message_sheet_button),
            buttonType = ButtonType.Primary,
            onClick = component::onMessageBottomSheetClick
        )
    }
}

@Composable
private fun CustomDialogContent(
    data: DialogsDemoDialogData,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = CustomTheme.colors.background.screen
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(data.title),
                color = CustomTheme.colors.text.primary,
                style = CustomTheme.typography.title.regular
            )

            Text(
                text = stringResource(data.message),
                color = CustomTheme.colors.text.primary,
                style = CustomTheme.typography.body.regular
            )

            AppButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(data.buttonText),
                buttonType = ButtonType.Primary,
                onClick = onCancelClick
            )
        }
    }
}

@Composable
private fun DemoBottomSheetContent(
    data: DialogsDemoBottomSheetData,
    onCancelClick: () -> Unit,
    onShowMessageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(data.title),
                color = CustomTheme.colors.text.primary,
                style = CustomTheme.typography.title.regular
            )

            Text(
                text = stringResource(data.message),
                color = CustomTheme.colors.text.primary,
                style = CustomTheme.typography.body.regular
            )

            if (data.showMessageButton && data.messageButtonText != null) {
                AppButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .noOverlapByMessage(),
                    text = stringResource(data.messageButtonText),
                    buttonType = ButtonType.Primary,
                    onClick = onShowMessageClick
                )
            }

            AppButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(data.cancelButtonText),
                buttonType = ButtonType.Secondary,
                onClick = onCancelClick
            )
        }
    }
}

@Preview
@Composable
private fun DialogsDemoUiPreview() {
    AppTheme {
        DialogsDemoUi(FakeDialogsDemoComponent())
    }
}
