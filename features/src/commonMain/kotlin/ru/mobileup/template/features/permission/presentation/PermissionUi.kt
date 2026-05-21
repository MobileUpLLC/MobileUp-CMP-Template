package ru.mobileup.template.features.permission.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.mobileup.template.core.dialog.standard.StandardDialog
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.theme.custom.CustomTheme
import ru.mobileup.template.core.utils.plus
import ru.mobileup.template.core.widget.button.AppButton
import ru.mobileup.template.core.widget.button.ButtonType
import ru.mobileup.template.core.widget.toolbar.AppToolbar
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.permission_camera_button
import ru.mobileup.template.features.generated.resources.permission_camera_status
import ru.mobileup.template.features.generated.resources.permission_title

@Composable
fun PermissionUi(
    component: PermissionComponent,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppToolbar(
                title = stringResource(Res.string.permission_title),
                showBackButton = true
            )
        }
    ) { innerPadding ->
        PermissionContent(
            component = component,
            innerPadding = innerPadding
        )
    }

    StandardDialog(component.settingsDialogControl)
}

@Composable
private fun PermissionContent(
    component: PermissionComponent,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val permissionState by component.permissionState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(innerPadding + PaddingValues(16.dp)),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PermissionStatus(
            isCameraPermissionGranted = permissionState.isCameraPermissionGranted
        )

        AppButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.permission_camera_button),
            buttonType = ButtonType.Primary,
            onClick = component::onCameraPermissionClick
        )
    }
}

@Composable
private fun PermissionStatus(
    isCameraPermissionGranted: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.fillMaxWidth(),
        text = stringResource(
            Res.string.permission_camera_status,
            isCameraPermissionGranted.toString()
        ),
        color = CustomTheme.colors.text.primary,
        style = CustomTheme.typography.body.regular
    )
}

@Preview
@Composable
private fun PermissionUiPreview() {
    AppTheme {
        PermissionUi(FakePermissionComponent())
    }
}
