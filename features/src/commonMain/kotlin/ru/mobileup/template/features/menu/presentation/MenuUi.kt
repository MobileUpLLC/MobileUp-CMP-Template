package ru.mobileup.template.features.menu.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.utils.plus
import ru.mobileup.template.core.widget.button.AppButton
import ru.mobileup.template.core.widget.button.ButtonType
import ru.mobileup.template.core.widget.toolbar.AppToolbar
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.menu_dialogs
import ru.mobileup.template.features.generated.resources.menu_permission
import ru.mobileup.template.features.generated.resources.menu_places
import ru.mobileup.template.features.generated.resources.menu_pokemons
import ru.mobileup.template.features.generated.resources.menu_settings
import ru.mobileup.template.features.generated.resources.menu_title
import ru.mobileup.template.features.menu.domain.Sample

@Composable
fun MenuUi(
    component: MenuComponent,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppToolbar(title = stringResource(Res.string.menu_title))
        }
    ) { innerPadding ->
        MenuContent(
            component = component,
            innerPadding = innerPadding
        )
    }
}

@Composable
private fun MenuContent(
    component: MenuComponent,
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
        component.samples.forEach { sample ->
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(sample.title),
                buttonType = ButtonType.Primary,
                onClick = { component.onSampleClick(sample) }
            )
        }
    }
}

private val Sample.title: StringResource
    get() = when (this) {
        Sample.Pokemons -> Res.string.menu_pokemons
        Sample.Dialogs -> Res.string.menu_dialogs
        Sample.Permission -> Res.string.menu_permission
        Sample.Settings -> Res.string.menu_settings
        Sample.Places -> Res.string.menu_places
    }

@Preview
@Composable
private fun MenuUiPreview() {
    AppTheme {
        MenuUi(FakeMenuComponent())
    }
}
