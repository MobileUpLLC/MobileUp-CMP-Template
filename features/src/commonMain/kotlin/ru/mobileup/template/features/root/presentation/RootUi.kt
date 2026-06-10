package ru.mobileup.template.features.root.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import ru.mobileup.template.core.message.presentation.MessageUi
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.utils.ConfigureSystemBars
import ru.mobileup.template.core.utils.LocalSystemBarsSettings
import ru.mobileup.template.core.utils.accumulate
import ru.mobileup.template.core.utils.predictiveBackAnimation
import ru.mobileup.template.features.dialogs.presentation.DialogsUi
import ru.mobileup.template.features.menu.presentation.MenuUi
import ru.mobileup.template.features.permission.presentation.PermissionUi
import ru.mobileup.template.features.pokemons.presentation.PokemonsUi
import ru.mobileup.template.features.pomodoro.presentation.PomodoroUi
import ru.mobileup.template.features.settings.presentation.SettingsUi

@Composable
fun RootUi(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    val childStack by component.childStack.collectAsState()
    val systemBarsSettings = LocalSystemBarsSettings.current.accumulate()

    Children(
        stack = childStack,
        modifier = modifier,
        animation = component.predictiveBackAnimation()
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Menu -> MenuUi(instance.component)
            is RootComponent.Child.Pokemons -> PokemonsUi(instance.component)
            is RootComponent.Child.Dialogs -> DialogsUi(instance.component)
            is RootComponent.Child.Permission -> PermissionUi(instance.component)
            is RootComponent.Child.Settings -> SettingsUi(instance.component)
            is RootComponent.Child.Pomodoro -> PomodoroUi(instance.component)
        }
    }

    MessageUi(
        component = component.messageComponent,
        bottomPadding = 16.dp
    )

    ConfigureSystemBars(systemBarsSettings)
}

@Preview
@Composable
private fun RootUiPreview() {
    AppTheme {
        RootUi(FakeRootComponent())
    }
}
