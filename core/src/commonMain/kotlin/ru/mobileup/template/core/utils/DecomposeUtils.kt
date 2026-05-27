package ru.mobileup.template.core.utils

import androidx.compose.runtime.State
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.statekeeper.StateKeeperOwner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.KSerializer
import ru.mobileup.template.core.dialog.DialogControl

/**
 * Creates a [ChildStack] with a single active component. Should be used to create a stack for Jetpack Compose preview.
 */
fun <T : Any> createFakeChildStack(instance: T): ChildStack<*, T> {
    return ChildStack(
        configuration = "<fake>",
        instance = instance
    )
}

fun <T : Any> createFakeChildStackStateFlow(instance: T): StateFlow<ChildStack<*, T>> {
    return MutableStateFlow(createFakeChildStack(instance))
}

/**
 * Creates a [ChildSlot] with given [configuration] and [instance]. Should be used to create a slot for Jetpack Compose preview.
 */
fun <C : Any, T : Any> createFakeChildSlot(
    configuration: C,
    instance: T,
): StateFlow<ChildSlot<C, T>> {
    return MutableStateFlow(
        ChildSlot(
            Child.Created(
                configuration = configuration,
                instance = instance
            )
        )
    )
}

/**
 * Converts [Value] from Decompose to [State] from Jetpack Compose.
 */
fun <T : Any> Value<T>.toStateFlow(lifecycle: Lifecycle): StateFlow<T> {
    val state: MutableStateFlow<T> = MutableStateFlow(this.value)

    if (lifecycle.state != Lifecycle.State.DESTROYED) {
        val observer = { value: T -> state.value = value }
        val cancellation = subscribe(observer)
        lifecycle.doOnDestroy {
            cancellation.cancel()
        }
    }

    return state
}

/**
 * Creates a coroutine scope tied to Decompose lifecycle. A scope is canceled when a component is destroyed.
 */
val ComponentContext.componentScope: CoroutineScope
    get() = (instanceKeeper.get(ComponentScopeKey) as? CoroutineScopeHolder)?.scope
        ?: replicaObserverHostWithProvidedDispatcher()
            .observerCoroutineScope
            .also { newScope ->
                instanceKeeper.put(ComponentScopeKey, CoroutineScopeHolder(newScope))
            }

private object ComponentScopeKey

private class CoroutineScopeHolder(val scope: CoroutineScope) : InstanceKeeper.Instance

/**
 * A helper function to save and restore component state.
 */
inline fun <T : Any> StateKeeperOwner.persistent(
    key: String = "PersistentState",
    serializer: KSerializer<T>,
    noinline save: () -> T,
    restore: (T) -> Unit,
) {
    stateKeeper.consume(key, serializer)?.run(restore)
    stateKeeper.register(key, serializer, save)
}

/**
 * Pushes a new configuration onto the navigation stack safely.
 *
 * This method ensures that the given configuration is removed from the stack first (if it exists),
 * and then re-added to ensure it appears at the top of the stack. This avoids duplicate entries
 * for the same configuration and ensures it is effectively "re-pushed" to the stack.
 *
 * @param configuration the configuration to push onto the stack.
 * @param onComplete called when the navigation is finished (either synchronously or asynchronously).
 */
fun <C : Any> StackNavigator<C>.safePush(configuration: C, onComplete: () -> Unit = {}) {
    navigate(
        transformer = { it - configuration + configuration },
        onComplete = { _, _ -> onComplete() }
    )
}

/**
 * Returns the currently active child instance from the child stack.
 */
val ChildStack<*, *>.activeChild: Any
    get() = active.instance

/**
 * Returns the currently active child instance from the current child stack value.
 */
val StateFlow<ChildStack<*, *>>.activeChild: Any
    get() = value.activeChild

/**
 * Returns the currently active child instance from the dialog slot.
 */
val <T : Any> DialogControl<*, T>.activeChild: T?
    get() = dialogSlot.value.child?.instance

/**
 * Returns all child instances of type [C] from the child stack.
 */
inline fun <reified C : Any> ChildStack<*, *>.getChildren(): List<C> =
    items.map { it.instance }.filterIsInstance<C>()

/**
 * Returns all child instances of type [C] from the current child stack value.
 */
inline fun <reified C : Any> StateFlow<ChildStack<*, *>>.getChildren(): List<C> =
    value.getChildren<C>()

/**
 * Retrieves the last child of type [C] from the child stack.
 * It will return `null` if no matching child is found.
 */
inline fun <reified C : Any> ChildStack<*, *>.getChild(): C? =
    getChildren<C>().lastOrNull()

/**
 * Retrieves the last child of type [C] from the child stack.
 * It will return `null` if no matching child is found.
 */
inline fun <reified C : Any> StateFlow<ChildStack<*, *>>.getChild(): C? =
    value.getChild<C>()

/**
 * Set [CoroutineDispatcher] for testing
 */
fun ComponentContext.setComponentCoroutineDispatcher(dispatcher: CoroutineDispatcher) {
    instanceKeeper.put(
        key = ComponentCoroutineDispatcherKey,
        instance = ComponentCoroutineDispatcherHolder(dispatcher)
    )
}

val ComponentContext.componentCoroutineDispatcher get() =
    (instanceKeeper.get(ComponentCoroutineDispatcherKey) as? ComponentCoroutineDispatcherHolder)?.dispatcher
        ?: Dispatchers.Main.immediate

private object ComponentCoroutineDispatcherKey

private class ComponentCoroutineDispatcherHolder(
    val dispatcher: CoroutineDispatcher
) : InstanceKeeper.Instance
