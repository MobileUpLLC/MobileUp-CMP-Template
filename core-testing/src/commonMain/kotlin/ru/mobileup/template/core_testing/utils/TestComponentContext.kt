package ru.mobileup.template.core_testing.utils

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.create
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.start

/**
 * Test-friendly [ComponentContext] with controllable lifecycle.
 */
class TestComponentContext(
    override val lifecycle: LifecycleRegistry = LifecycleRegistry()
) : ComponentContext by DefaultComponentContext(lifecycle) {

    /**
     * Moves lifecycle to [lifecycleState].
     *
     * [LifecycleRegistry] performs intermediate transitions internally.
     */
    fun moveToState(lifecycleState: Lifecycle.State) {
        when (lifecycleState) {
            Lifecycle.State.INITIALIZED -> Unit
            Lifecycle.State.CREATED -> lifecycle.create()
            Lifecycle.State.STARTED -> lifecycle.start()
            Lifecycle.State.RESUMED -> lifecycle.resume()
            Lifecycle.State.DESTROYED -> lifecycle.destroy()
        }
    }
}
