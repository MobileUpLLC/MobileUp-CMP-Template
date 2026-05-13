package ru.mobileup.template.features.root.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.template.core.message.presentation.MessageComponent
import ru.mobileup.template.core.utils.PredictiveBackComponent
import ru.mobileup.template.features.pokemons.presentation.PokemonsComponent

/**
 * A root of a Decompose component tree.
 *
 * Note: Try to minimize child count in RootComponent. It should operate by flows of screens rather than separate screens.
 */
interface RootComponent : PredictiveBackComponent {

    val childStack: StateFlow<ChildStack<*, Child>>

    val messageComponent: MessageComponent

    sealed interface Child {
        class Pokemons(val component: PokemonsComponent) : Child
    }
}
