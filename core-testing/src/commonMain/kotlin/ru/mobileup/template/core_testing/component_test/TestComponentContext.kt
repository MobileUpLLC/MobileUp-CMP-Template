package ru.mobileup.template.core_testing.component_test

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ComponentContextFactory
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.create
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.start
import kotlinx.coroutines.CoroutineDispatcher
import ru.mobileup.template.core.utils.setComponentCoroutineDispatcher

/**
 * Test-friendly [ComponentContext] with controllable lifecycle and testing [CoroutineDispatcher].
 */
class TestComponentContext(
    coroutineDispatcher: CoroutineDispatcher,
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(),
    private val componentContext: ComponentContext = DefaultComponentContext(lifecycleRegistry)
) : ComponentContext by componentContext {

    init {
        setComponentCoroutineDispatcher(coroutineDispatcher)
    }

    /**
     * Moves lifecycle to [lifecycleState].
     *
     * [LifecycleRegistry] performs intermediate transitions internally.
     */
    fun moveToState(lifecycleState: Lifecycle.State) {
        when (lifecycleState) {
            Lifecycle.State.INITIALIZED -> Unit
            Lifecycle.State.CREATED -> lifecycleRegistry.create()
            Lifecycle.State.STARTED -> lifecycleRegistry.start()
            Lifecycle.State.RESUMED -> lifecycleRegistry.resume()
            Lifecycle.State.DESTROYED -> lifecycleRegistry.destroy()
        }
    }

    override val componentContextFactory: ComponentContextFactory<ComponentContext> =
        ComponentContextFactory { lifecycle, stateKeeper, instanceKeeper, backHandler ->
            val context = componentContext.componentContextFactory(
                lifecycle,
                stateKeeper,
                instanceKeeper,
                backHandler
            )
            TestChildComponentContext(context, coroutineDispatcher)
        }
}

private class TestChildComponentContext(
    private val componentContext: ComponentContext,
    private val coroutineDispatcher: CoroutineDispatcher
) : ComponentContext by componentContext {

    init {
        setComponentCoroutineDispatcher(coroutineDispatcher)
    }

    override val componentContextFactory: ComponentContextFactory<ComponentContext> =
        ComponentContextFactory { lifecycle, stateKeeper, instanceKeeper, backHandler ->
            val context = componentContext.componentContextFactory(
                lifecycle,
                stateKeeper,
                instanceKeeper,
                backHandler
            )
            TestChildComponentContext(context, coroutineDispatcher)
        }
}
