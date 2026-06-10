package ru.mobileup.template.features.root.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.template.core.message.presentation.MessageComponent
import ru.mobileup.template.core.utils.PredictiveBackComponent
import ru.mobileup.template.features.dialogs.presentation.DialogsComponent
import ru.mobileup.template.features.menu.presentation.MenuComponent
import ru.mobileup.template.features.permission.presentation.PermissionComponent
import ru.mobileup.template.features.pokemons.presentation.PokemonsComponent
import ru.mobileup.template.features.pomodoro.presentation.PomodoroComponent
import ru.mobileup.template.features.settings.presentation.SettingsComponent

/**
 * A root of a Decompose component tree.
 *
 * Note: Try to minimize child count in RootComponent. It should operate by flows of screens rather than separate screens.
 */
interface RootComponent : PredictiveBackComponent {

    val childStack: StateFlow<ChildStack<*, Child>>

    val messageComponent: MessageComponent

    sealed interface Child {
        class Menu(val component: MenuComponent) : Child
        class Pokemons(val component: PokemonsComponent) : Child
        class Dialogs(val component: DialogsComponent) : Child
        class Permission(val component: PermissionComponent) : Child
        class Settings(val component: SettingsComponent) : Child
        class Pomodoro(val component: PomodoroComponent) : Child
    }
}
