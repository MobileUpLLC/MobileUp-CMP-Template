package ru.mobileup.template.features.utils

import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import io.kotest.core.test.testCoroutineScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.Koin
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.core_testing.coreTestModule
import ru.mobileup.template.core_testing.scope.IntegrationTestScope
import ru.mobileup.template.core_testing.scope.IntegrationTestScopeImpl
import ru.mobileup.template.features.featureModules

// TODO: remove this, workaround after moving replica-decompose
private val integrationTestMainDispatcherMutex = Mutex()

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
suspend fun FunSpecContainerScope.integrationTest(
    name: String,
    block: suspend IntegrationTestScope.() -> Unit,
) {
    test(name).config(coroutineTestScope = true) {
        integrationTestMainDispatcherMutex.withLock {
            val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
            // TODO: remove Dispatchers.setMain workaround after moving replica-decompose
            // lifecycle scope off Dispatchers.Main in unit-test infrastructure.
            Dispatchers.setMain(testDispatcher)
            val koin = createKoin(testDispatcher)

            val integrationScope = IntegrationTestScopeImpl(
                koin = koin,
                kotestScope = this,
                testScheduler = testCoroutineScheduler
            )

            try {
                integrationScope.block()
            } finally {
                // TODO: remove                 testCoroutineScheduler.advanceUntilIdle() workaround after moving replica-decompose
                testCoroutineScheduler.advanceUntilIdle()
                koin.close()
                Dispatchers.resetMain()
            }
        }
    }
}

private fun createKoin(testDispatcher: TestDispatcher): Koin {
    return Koin().apply {
        loadModules(coreTestModule(testDispatcher) + featureModules)
        declare(ComponentFactory(this))
        createEagerInstances()
    }
}
